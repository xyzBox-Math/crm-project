package com.bjpowernode.crm.commons.domain;

public class ReturnObject {
    /**
     * 登录状态码
     * */
    String code;
    /**
     * 登录信息
     * */
    String message;
    /**
     * 其他信息
     * */
    Object otherImf;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getOtherImf() {
        return otherImf;
    }

    public void setOtherImf(Object otherImf) {
        this.otherImf = otherImf;
    }
}
