package com.animal.dialogue.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/8/25 21:03
 */
@Data
public class SessionVO implements Serializable {

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
