package com.h.rxjava2retrofit2succinct.bean;

import java.io.Serializable;

/**
 * Created by hs on 2017/8/10.
 */

public class BaseEntity<T> implements Serializable {


    /**
     * code :
     * message :
     * resultData : {}
     * success :
     * token :
     */

    private String code;
    private String message;
    public T resultData;
    private boolean success;
    private String token;

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


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
