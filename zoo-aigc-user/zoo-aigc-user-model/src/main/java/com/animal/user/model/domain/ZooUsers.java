package com.animal.user.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 
 * @TableName zoo_users
 */
@TableName(value ="zoo_users")
@Data
public class ZooUsers implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 账户邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邀请码
     */
    private String remember_token;

    /**
     * 邮箱确认码
     */
    private String invite_code;

    /**
     * 手机
     */
    private String phone;

    /**
     * 个人介绍
     */
    private String description;

    /**
     * 个人ip地址
     */
    private String ip;

    /**
     * 剩余额度
     */
    private BigDecimal money;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 更新时间
     */
    private Timestamp updateTime;

    /**
     * 手机验证码
     */
    private String phone_code;

    /**
     * 是否删除 1是 0否
     */
    private Integer is_delete;

    /**
     * 微信Id
     */
    private String wx_openId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}