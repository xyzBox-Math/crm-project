package com.bjpowernode.crm.commons.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;

/**
 * 以字符串形式返回excel列中的元素
 */
public class HSSFUtils {
    public static String getCellValueToStr(HSSFCell hssfCell){
        String ret="";
        if(hssfCell.getCellType()==HSSFCell.CELL_TYPE_STRING)
            ret=hssfCell.getStringCellValue();
        else if(hssfCell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC)
            ret=hssfCell.getNumericCellValue()+"";
        else
            ret="";
        return ret;
    }
}
