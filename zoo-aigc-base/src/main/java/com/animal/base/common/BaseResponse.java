package com.animal.base.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/5/30 18:40
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;
    private String message;
    private String description;
    private T Data;

    public BaseResponse(int code,T data,String message,String description){
        this.code = code;
        this.Data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code,T data,String message){
        this(code,data,message,"");
    }

    public BaseResponse(int code,String message){
        this(code,null,message,"");
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null, errorCode.getMessage(), errorCode.getDescription());
    }

}
