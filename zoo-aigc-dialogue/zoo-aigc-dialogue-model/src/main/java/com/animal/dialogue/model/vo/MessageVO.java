package com.animal.dialogue.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/8/25 21:19
 */
@Data
public class MessageVO implements Serializable {

    private Long id;

    /**
     * 问题
     */
    private String question;

    /**
     * 回答
     */
    private String message;


    /**
     * 消息来源
     */
    private String type;

    /**
     * AI对话头像
     */
    private String icon;
}
