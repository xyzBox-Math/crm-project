package com.bjpowernode.crm.commons.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    /**
     * 对时间进行格式化:yyyy-MM-dd HH:mm:ss
     */
    public static String formatDateTime(Date date){
        Date nowStr=new Date();
        SimpleDateFormat simpleFormatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now=simpleFormatter.format(nowStr);
        return now;
    }
}
