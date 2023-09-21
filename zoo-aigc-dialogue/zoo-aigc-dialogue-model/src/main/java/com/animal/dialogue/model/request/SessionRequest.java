package com.animal.dialogue.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/8/26 1:24
 */
@Data
public class SessionRequest implements Serializable {
    /**
     * 用户id
     */
    private Integer user_id;

    /**
     * id
     */
    private String id;

    /**
     * 用户自定义标题
     */
    private String title;
}
