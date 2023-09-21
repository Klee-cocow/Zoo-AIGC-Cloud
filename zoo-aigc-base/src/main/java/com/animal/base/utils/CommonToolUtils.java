package com.animal.base.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/7/19 19:23
 */
@Component
@Slf4j
public class CommonToolUtils {

    private static final String TOKEN = "zooaigc595"; //自定义token



    // 获取用户当前的真实ip
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("PRoxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    public static Timestamp getCurrentDate() {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        return timestamp;
    }

    /**
     * 校验微信服务器Token签名
     *
     * @param signature 微信加密签名
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return boolean
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce) throws NoSuchAlgorithmException {
        String[] arr = {TOKEN, timestamp, nonce};
        Arrays.sort(arr);
        StringBuilder stringBuilder = new StringBuilder();
        for (String param : arr) {
            stringBuilder.append(param);
        }
        String hexString = SHA1(stringBuilder.toString());
        return signature.equals(hexString);
    }

    private static String SHA1(String str) throws NoSuchAlgorithmException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(str.getBytes());
            return toHexString(digest);
        } catch (NoSuchAlgorithmException e) {
            log.info("校验令牌Token出现错误：{}", e.getMessage());
        }
        return "";
    }
    /**
     * 字节数组转化为十六进制
     *
     * @param digest 字节数组
     * @return String
     */
    private static String toHexString(byte[] digest) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            String shaHex = Integer.toHexString(b & 0xff);
            if (shaHex.length() < 2) {
                hexString.append(0);
            }
            hexString.append(shaHex);
        }
        return hexString.toString();
    }

    public static Map<String,String> xmlToMap(HttpServletRequest request) throws IOException, DocumentException {
        Map<String,String> map = new HashMap<>();
        SAXReader reader = new SAXReader();
        InputStream inputStream = request.getInputStream();
        Document doc = reader.read(inputStream);
        Element rootElement = doc.getRootElement();

        List<Element> elements = rootElement.elements();
        for(Element item : elements){
            map.put(item.getName(),item.getText());
        }
        return map;
    }


    /**
     * 设置回复消息xml格式
     */
    public static String getXmlString(Map<String, String> map, String content) {
        String xml = "";
        if (map != null) {
            xml = "<xml>";
            xml += "<ToUserName><![CDATA[";
            xml += map.get("FromUserName");
            xml += "]]></ToUserName>";
            xml += "<FromUserName><![CDATA[";
            xml += map.get("ToUserName");
            xml += "]]></FromUserName>";
            xml += "<CreateTime>";
            xml += System.currentTimeMillis();
            xml += "</CreateTime>";
            xml += "<MsgType><![CDATA[";
            xml += "text";
            xml += "]]></MsgType>";
            xml += "<Content><![CDATA[";
            xml += content;
            xml += "]]></Content>";
            xml += "</xml>";
        }
        log.info("xml封装结果=>" + xml);
        return xml;
    }

}

