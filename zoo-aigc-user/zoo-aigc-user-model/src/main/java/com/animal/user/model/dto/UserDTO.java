package com.animal.user.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {

    /**
     * 账户邮箱
     */
    private String email;
    /**
     * 密码
     */
    private String password;

    /**
     * 手机验证码
     */
    private String phone_code;

    private String phone;

    /**
     * 邮箱确认码
     */
    private String invite_code;
}