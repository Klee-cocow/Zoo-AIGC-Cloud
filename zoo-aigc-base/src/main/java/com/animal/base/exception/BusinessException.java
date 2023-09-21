package com.animal.base.exception;

import com.animal.base.common.ErrorCode;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/5/30 18:51
 */
public class BusinessException extends RuntimeException {


    private int code;
    private String description;

    public BusinessException(int code, String message, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }
    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
