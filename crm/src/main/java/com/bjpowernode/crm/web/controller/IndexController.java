package com.bjpowernode.crm.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    @RequestMapping("/")
    public String index(){
        //请求转发
        return "index";
    }//返回值可以使用ModelAndView也可以使用String,方法权限是因为controller层由springmvc核心调用，与controller不在同一个包
}
