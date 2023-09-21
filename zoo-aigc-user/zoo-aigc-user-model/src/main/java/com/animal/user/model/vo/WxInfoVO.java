package com.animal.user.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/9/1 18:44
 */
@Data
public class WxInfoVO implements Serializable {
    private String QRUrl;
    private String expire;
    private String ticket;

}
