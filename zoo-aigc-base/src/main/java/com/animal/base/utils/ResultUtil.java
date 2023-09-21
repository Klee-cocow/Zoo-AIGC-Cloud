package com.animal.base.utils;

import com.animal.base.common.BaseResponse;
import com.animal.base.common.ErrorCode;
import com.animal.product.common.ErrorCode;

/**
 * @author 咏鹅
 * @version 1.0
 * @description 统一返回结果
 * @date 2023/5/30 18:56
 */
public class ResultUtil {

    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,data,"OK");
    }

    public static BaseResponse error(ErrorCode errorCode, String message, String description){
        return new BaseResponse<>(errorCode.getCode(),message,description);
    }

    public static BaseResponse error(int code,String message,String description){
        return new BaseResponse<>(code,null,message,description);
    }

    public static BaseResponse error(int code,String message){
        return new BaseResponse<>(code,message);
    }

    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

}
