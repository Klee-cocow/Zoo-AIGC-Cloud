package com.animal.user.model.entity;

import lombok.Data;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/8/30 21:57
 */
@Data
public class WxUserInfo {
    private String openid;
    private String nickname;
    private Integer sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;

}
