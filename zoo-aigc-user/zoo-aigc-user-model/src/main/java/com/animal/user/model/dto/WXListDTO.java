package com.animal.user.model.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wx")
public class WXListDTO {

    @Value("${wx.app_id}")
    String APP_ID;
    @Value("${wx.app_secret}")
    String APP_SECRET;
    @Value("${wx.redirect_url}")
    String REDIRECT_URL;
}
