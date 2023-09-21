package com.animal.dialogue.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 
 * @TableName zoo_message
 */
@TableName(value ="zoo_message")
@Data
public class ZooMessage implements Serializable {
    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 会话id
     */
    private String session_id;

    /**
     * 来源key的id
     */
    private Integer from_Key_id;

    /**
     * 问题
     */
    private String question;

    /**
     * 回答
     */
    private String message;

    /**
     * 创建时间
     */
    private Timestamp createTime;


    /**
     * 消息来源
     */
    private String type;

    /**
     * AI对话头像
     */
    private String icon;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}