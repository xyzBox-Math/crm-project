package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.server.UserServer;
import com.bjpowernode.crm.settings.server.impl.UserServerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    @Qualifier("userserver")
    private UserServer userServer;
    @RequestMapping("/settings/qx/user/toLogin.do")
    public String toLogin(){
        return "settings/qx/user/login";
    }

    @RequestMapping("/settings/qx/user/Login.do")
    @ResponseBody
    public Object Login(String loginAct, String loginPwd, String isRemPwd, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpSession httpSession){
        Map<String,Object>map=new HashMap<String, Object>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        User user=userServer.queryUserByLoginActAndPwd(map);
        ReturnObject returnObject=new ReturnObject();
        if(user==null){
            //用户名或者密码错误
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("用户名或者密码错误");
        }else{
            if(user.getExpireTime().compareTo(DateUtils.formatDateTime(new Date()))<0){
                //用户过期
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("用户过期");
            }else if("0".compareTo(user.getLockState())==0){
                //状态被锁定
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("状态被锁定");
            }else if(!user.getAllowIps().contains(httpServletRequest.getRemoteAddr())){
                //ip受限
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("ip受限");
            }else{
                //登陆成功
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
                httpSession.setAttribute(Contants.SESSION_USER,user);
                returnObject.setMessage("登陆成功");
                //是否记住密码
                if("true".equals(isRemPwd)){//加上判断否则每次都会重置cookie
                    Cookie cookie1=new Cookie("loginAct",loginAct);
                    cookie1.setMaxAge(10*24*60*60);
                    httpServletResponse.addCookie(cookie1);
                    Cookie cookie2=new Cookie("loginPwd",loginPwd);
                    cookie2.setMaxAge(10*24*60*60);
                    httpServletResponse.addCookie(cookie2);
                }else{//删除cookie
                    Cookie cookie1=new Cookie("loginAct","1");
                    cookie1.setMaxAge(0);
                    httpServletResponse.addCookie(cookie1);
                    Cookie cookie2=new Cookie("loginPwd","1");
                    cookie2.setMaxAge(0);
                    httpServletResponse.addCookie(cookie2);
                }
            }
        }
        return returnObject;
    }

    /**
     * 安全退出
     * @return
     */
    @RequestMapping("/settings/qx/user/loginOut.do")
    public String loginOut(HttpServletResponse httpServletResponse,HttpSession httpSession){
        //销毁cookie和session
        Cookie cookie = new Cookie("loginAct", "1");
        cookie.setMaxAge(0);
        httpServletResponse.addCookie(cookie);
        Cookie cookie1 = new Cookie("loginPwd", "1");
        cookie1.setMaxAge(0);
        httpServletResponse.addCookie(cookie1);
        httpSession.invalidate();
        return "redirect:/";
    }
}
