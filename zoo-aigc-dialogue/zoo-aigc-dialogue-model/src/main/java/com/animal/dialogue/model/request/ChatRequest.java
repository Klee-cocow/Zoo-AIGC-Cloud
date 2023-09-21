package com.animal.dialogue.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/8/1 18:44
 */
@Data
public class ChatRequest implements Serializable {

    private String message;

    //session id
    private String sid;

    private String question;

    //message id
    private Long mid;

    //用户id
    private Integer uid;
}
