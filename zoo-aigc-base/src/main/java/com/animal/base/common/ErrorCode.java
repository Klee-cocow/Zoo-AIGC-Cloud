package com.animal.base.common;

/**
 * @author 咏鹅
 * @version 1.0
 * @description 自定义状态码
 * @date 2023/5/30 18:45
 */
public enum ErrorCode {

    SUCCESS(0, "成功", ""),
    NO_LOGIN(1000,"没有登陆",""),
    NO_SAVE(4100,"保存失败",""),
    SYSTEM_ERROR(5000,"系统错误","请联系管理员"),
    NO_AUTH(1234,"没有权限",""),
    NO_QUERY(3255,"没有找到",""),
    PARAMETER_ERROR(999,"参数错误",""),
    OVER_FLOW(777,"栈溢出",""),
    OUT_OF_BOUNDS(12306,"数组越界",""),
    HTTP_ERROR(1001,"HTTP请求错误",""),
    ;

    private int code;


    private String message;
    private String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }


}
