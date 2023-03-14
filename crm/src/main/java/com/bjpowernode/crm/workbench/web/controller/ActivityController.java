package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.server.UserServer;
import com.bjpowernode.crm.settings.server.impl.UserServerImpl;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ActivityController {
    @Autowired
    @Qualifier("userserver")
    private UserServer userServer;
    @Autowired
    @Qualifier("activityservice")
    private ActivityService activityService;
    @RequestMapping("/workbench/activity/index.do")
    public String index(HttpServletRequest httpServletRequest){
        httpServletRequest.setAttribute("userList",userServer.queryAllUsers());
        return "workbench/activity/index";
    }
    @RequestMapping("/workbench/activity/saveCreateActivity.do")
    public @ResponseBody Object saveCreateActivity(Activity activity, HttpSession httpSession){
        /**
         * 二次封装参数
         */
        ReturnObject returnObject=new ReturnObject();
        User user = (User) httpSession.getAttribute(Contants.SESSION_USER);
        activity.setId(UUIDUtils.getUUID());
        activity.setCreateTime(DateUtils.formatDateTime(new Date()));
        activity.setCreateBy(user.getId());
        /**
         * 对数据进行增删改需要处理异常
         */
        try{
            int ret=activityService.saveCreateActivity(activity);
            if(ret==0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试...");
            }
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
        }catch(Exception e){
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }
    @RequestMapping("/workbench/activity/queryActivity.do")
    public @ResponseBody Object queryActivity(String name,String owner,String startDate,String endDate,
                                                int pageNo,int pageSize){
        /**
         * 封装参数
         */
        Map<String,Object>map=new HashMap<String, Object>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("beginNo",(pageNo-1)*pageSize);
        map.put("pageSize",pageSize);
        /**
         * 调用service
         */

        List<Activity> activities = activityService.queryActivityByConditionForPage(map);
        int i = activityService.queryCountByCondition(map);
        /**
         * 封装响应
         */

        Map<String,Object>map1=new HashMap<String, Object>();
        map1.put("listActivity",activities);
        map1.put("totalRows",i);
        return map1;
    }
    @RequestMapping("/workbench/activity/deleteActivityByIds.do")
    public @ResponseBody Object deleteActivityByIds(String ids){
        ReturnObject returnObject=new ReturnObject();
        try{
            /**
             * 拆解字符串,因为我的写法不是拼参数字符串而是直接发送一串长字符串
             */

            String[] str=ids.split(",");
            for(int i=0;i<str.length;i++)
                str[i]=str[i].trim();
            int ret=activityService.deleteActivityByIds(str);
            if(ret>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else{
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试...");
                /*returnObject.setMessage(ids[0]);*/
            }
        }catch(Exception e){
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }
    @RequestMapping("/workbench/activity/queryActivityById.do")
    public @ResponseBody Object queryActivityById(String id){
        Activity activity= activityService.queryActivityById(id);
       /* Map<String,Object>map=new HashMap<String,Object>();
        map.put("id",activity.getId());
        map.put("owner",activity.getOwner());
        map.put("startDate",activity.getStartDate());
        map.put("endDate",activity.getEndDate());
        map.put("cost",activity.getCost());
        map.put("description",activity.getDescription());*/
        return activity;
    }
    @RequestMapping("/workbench/activity/saveUpDateActivity.do")
    public @ResponseBody  Object saveUpDateActivity(Activity activity,HttpSession httpSession){
        /**
         * 封装参数
         */
        User user = (User)httpSession.getAttribute(Contants.SESSION_USER);
        activity.setEditBy(user.getId());
        activity.setEditTime(DateUtils.formatDateTime(new Date()));
        /**
         * 调用修改业务
         */
        ReturnObject returnObject=new ReturnObject();
        try{
            int ret=activityService.saveUpDateActivity(activity);
            if(ret>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else{
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试....");
            }
        }catch(Exception e){
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }
        return returnObject;
    }

    /**
     * 返回excel文件,下载到客户端
     * @param httpServletResponse
     */
    @RequestMapping("/workbench/activity/queryAllActivitiesToExcel.do")
    public void queryAllActivitiesToExcel(HttpServletResponse httpServletResponse) throws Exception{
        //得到所有市场活动
        List<Activity>activityList= activityService.queryAllActivities();
        //创建excel对象
        HSSFWorkbook wb=new HSSFWorkbook();
        //创建sheet
        HSSFSheet sheet=wb.createSheet("市场活动表");
        //创建表头
        HSSFRow row=sheet.createRow(0);
        HSSFCell cell=row.createCell(0);
        cell.setCellValue("ID");

        cell=row.createCell(1);
        cell.setCellValue("所有者");
        cell=row.createCell(2);
        cell.setCellValue("名称");
        cell=row.createCell(3);
        cell.setCellValue("开始日期");
        cell=row.createCell(4);
        cell.setCellValue("结束日期");
        cell=row.createCell(5);
        cell.setCellValue("成本");
        cell=row.createCell(6);
        cell.setCellValue("备注");
        cell=row.createCell(7);
        cell.setCellValue("创建时间");
        cell=row.createCell(8);
        cell.setCellValue("创建者");
        cell=row.createCell(9);
        cell.setCellValue("修改时间");
        cell=row.createCell(10);
        cell.setCellValue("修改者");

        /**
         * 对链表进行检查，存在内容才开始遍历
         */
        Activity activity=new Activity();
        if(activityList!=null&&activityList.size()>0){
            int length=activityList.size();
            for(int i=0;i<length;i++){
                activity=activityList.get(i);
                row=sheet.createRow(i+1);
                cell=row.createCell(0);
                cell.setCellValue(activity.getId());

                cell=row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell=row.createCell(2);
                cell.setCellValue(activity.getName());
                cell=row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell=row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell=row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell=row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell=row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell=row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell=row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell=row.createCell(10);
                cell.setCellValue(activity.getEditBy());
            }
        }
        //优化直接建立wb到输出流，无需经过磁盘中转
        //创建真正的文件
       /* OutputStream os=new FileOutputStream("D:\\excelFileLoadTest\\ActivityList.xls");
        wb.write(os);
        //关闭资源
        os.close();
        wb.close();*/
        //设置响应类型
        httpServletResponse.setContentType("application/octet-stream;charset=UTF-8");
        //设置返回文件时直接打开下载窗口
        httpServletResponse.addHeader("Content-Disposition","attachment;filename=ActivityList.xls");
        //获取响应输出流
        OutputStream out=httpServletResponse.getOutputStream();
        //把文件读入到输入流
       /* InputStream in=new FileInputStream("D:\\excelFileLoadTest\\ActivityList.xls");
        byte[] buff=new byte[256];
        int len=0;
        while((len=in.read(buff))!=-1){
            out.write(buff,0,len);
        }
        in.close();*/
        wb.write(out);
        out.flush();
    }

    /**
     * 选择导出
     * @param ids
     * @param httpServletResponse
     */
    @RequestMapping("/workbench/activity/querySomeActivities.do")
    public void querySomeActivities(String ids,HttpServletResponse httpServletResponse)throws Exception{
        String[] str=ids.split(",");
        for(int i=0;i<str.length;i++)
            str[i]=str[i].trim();
        List<Activity> activityList = activityService.querySomeActivities(str);
        //创建excel对象
        HSSFWorkbook wb=new HSSFWorkbook();
        //创建sheet
        HSSFSheet sheet=wb.createSheet("市场活动表");
        //创建表头
        HSSFRow row=sheet.createRow(0);
        HSSFCell cell=row.createCell(0);
        cell.setCellValue("ID");

        cell=row.createCell(1);
        cell.setCellValue("所有者");
        cell=row.createCell(2);
        cell.setCellValue("名称");
        cell=row.createCell(3);
        cell.setCellValue("开始日期");
        cell=row.createCell(4);
        cell.setCellValue("结束日期");
        cell=row.createCell(5);
        cell.setCellValue("成本");
        cell=row.createCell(6);
        cell.setCellValue("备注");
        cell=row.createCell(7);
        cell.setCellValue("创建时间");
        cell=row.createCell(8);
        cell.setCellValue("创建者");
        cell=row.createCell(9);
        cell.setCellValue("修改时间");
        cell=row.createCell(10);
        cell.setCellValue("修改者");

        /**
         * 对链表进行检查，存在内容才开始遍历
         */
        Activity activity=new Activity();
        if(activityList!=null&&activityList.size()>0){
            int length=activityList.size();
            for(int i=0;i<length;i++){
                activity=activityList.get(i);
                row=sheet.createRow(i+1);
                cell=row.createCell(0);
                cell.setCellValue(activity.getId());

                cell=row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell=row.createCell(2);
                cell.setCellValue(activity.getName());
                cell=row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell=row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell=row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell=row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell=row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell=row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell=row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell=row.createCell(10);
                cell.setCellValue(activity.getEditBy());
            }
        }
        //优化直接建立wb到输出流，无需经过磁盘中转
        //创建真正的文件
       /* OutputStream os=new FileOutputStream("D:\\excelFileLoadTest\\ActivityList.xls");
        wb.write(os);
        //关闭资源
        os.close();
        wb.close();*/
        //设置响应类型
        httpServletResponse.setContentType("application/octet-stream;charset=UTF-8");
        //设置返回文件时直接打开下载窗口
        httpServletResponse.addHeader("Content-Disposition","attachment;filename=ActivityList.xls");
        //获取响应输出流
        OutputStream out=httpServletResponse.getOutputStream();
        //把文件读入到输入流
       /* InputStream in=new FileInputStream("D:\\excelFileLoadTest\\ActivityList.xls");
        byte[] buff=new byte[256];
        int len=0;
        while((len=in.read(buff))!=-1){
            out.write(buff,0,len);
        }
        in.close();*/
        wb.write(out);
        out.flush();
    }




}
