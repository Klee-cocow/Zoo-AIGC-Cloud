package com.animal.user.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 咏鹅
 * @version 1.0
 * @description 用户注册
 * @date 2023/7/19 18:04
 */
@Data
public class UserRegisterRequest implements Serializable {
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

    /**
     * 注册类型
     */
    private String registerIdentity;
}
