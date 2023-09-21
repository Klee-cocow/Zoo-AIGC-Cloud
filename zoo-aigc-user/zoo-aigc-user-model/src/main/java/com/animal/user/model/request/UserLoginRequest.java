package com.animal.user.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/7/19 18:07
 */
@Data
public class UserLoginRequest implements Serializable {
    /**
     * 账户邮箱
     */
    private String email;
    /**
     * 密码
     */
    private String password;

    /**
     * 登录类型
     */
    private String loginIdentity;
}
