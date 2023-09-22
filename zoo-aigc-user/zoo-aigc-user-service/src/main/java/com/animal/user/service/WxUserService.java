package com.animal.user.service;

import com.animal.user.model.vo.WxInfoVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dom4j.DocumentException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/8/30 22:21
 */
public interface WxUserService {

    String redirectUrl(HttpServletResponse response);

    WxInfoVO getWxQRCodeParam();

    String getWxUserInfo(String code,String state,HttpServletResponse response);

    String responseMsg(HttpServletRequest request, HttpServletResponse response) throws DocumentException;

    SseEmitter createSseConnect(String ticket) throws IOException;

    String wxQRCodeScan(String sid);
}
