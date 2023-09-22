package com.animal.user.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.animal.base.common.ErrorCode;
import com.animal.base.common.LocalCache;
import com.animal.base.constant.UserConstant;
import com.animal.base.exception.BusinessException;
import com.animal.base.utils.CommonToolUtils;
import com.animal.base.utils.HttpClientUtils;
import com.animal.base.utils.JwtUtil;
import com.animal.base.utils.SseEmitterUtils;
import com.animal.user.mapper.ZooUsersMapper;
import com.animal.user.model.domain.ZooUsers;
import com.animal.user.model.dto.WXListDTO;
import com.animal.user.model.entity.WxUserInfo;
import com.animal.user.model.vo.UserVO;
import com.animal.user.model.vo.WxInfoVO;
import com.animal.user.service.WxUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/8/30 22:22
 */
@Slf4j
@Service
public class WxUserServiceImpl implements WxUserService {
    @Resource
    private WXListDTO wxListDTO;

    @Resource
    private ZooUsersMapper usersMapper;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public WxInfoVO getWxQRCodeParam() {
        String getAccessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential" +
                "&appid=" + wxListDTO.getAPP_ID() +
                "&secret=" + wxListDTO.getAPP_SECRET();
        try {
            String accessTokenRes = HttpClientUtils.doGet(getAccessTokenUrl);
            log.info("accessTokenRes=>" + accessTokenRes);
            String accessToken = String.valueOf(JSON.parseObject(accessTokenRes).get("access_token")); // 获取到access_token

            // 通过access_token和一些参数发送post请求获取二维码Ticket
            String getTicketUrl = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + accessToken;
            Map<String, Object> map = Map.of(
                    "expire_seconds", "60",
                    "action_name", "QR_STR_SCENE",
                    "action_info", Map.of(
                            "scene_str","login"
                    )
            );
            String ticketData = HttpClientUtils.doPostJson(getTicketUrl, map);
            log.info("ticketData=>" + ticketData);
            JSONObject data = JSON.parseObject(ticketData);
            String ticket = String.valueOf(data.get("ticket"));
            String expire_seconds = String.valueOf(data.get("expire_seconds"));
            WxInfoVO wxInfoVO = new WxInfoVO();
            //通过ticket获取二维码url
            String encodeTicket = URLEncoder.encode(ticket, "utf-8"); // 编码ticket
            String getQRUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + encodeTicket;
            wxInfoVO.setQRUrl(getQRUrl);
            wxInfoVO.setExpire(expire_seconds);
            wxInfoVO.setTicket(ticket);
            return wxInfoVO; // 二维码url

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String redirectUrl(HttpServletResponse response) {
        String Url = "https://open.weixin.qq.com/connect/oauth2/authorize" +
                "?appid=" + wxListDTO.getAPP_ID() +
                "&redirect_uri=" + wxListDTO.getREDIRECT_URL() +
                "&response_type=code" +
                "&scope=snsapi_userinfo" +
                "&state=STATE" + "&connect_redirect=1#wechat_redirect";
        try {
            response.sendRedirect(Url); // 重定向url
        } catch (IOException e) {
            log.error("获取微信code失败: " + e.getMessage());
        }
        return "重定向成功";

    }

    @Override
    public String getWxUserInfo(String code, String state, HttpServletResponse response) {
        String userName = "";
        String Url = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=" + wxListDTO.getAPP_ID() +
                "&secret=" + wxListDTO.getAPP_SECRET() +
                "&code=" + code +
                "&grant_type=authorization_code";
        try {
            String accessKey = HttpClientUtils.doGet(Url);
            String accessToken = String.valueOf(JSONUtil.parseObj(accessKey).get("access_token"));
            String openId = String.valueOf(JSONUtil.parseObj(accessKey).get("openid"));

            // 获取用户信息
            String getUserUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=" + accessToken +
                    "&openid=" + openId +
                    "&lang=zh_CN";
            String userInfo = HttpClientUtils.doGet(getUserUrl);
            WxUserInfo wxUserInfo = JSON.parseObject(userInfo, WxUserInfo.class);
            userName = wxUserInfo.getNickname();
            return userName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String responseMsg(HttpServletRequest request, HttpServletResponse response) throws DocumentException {
        try {
            request.setCharacterEncoding("UTF-8");
            Map<String, String> msg = CommonToolUtils.xmlToMap(request);
            String fromUserName = msg.get("FromUserName"); // 这个就是你关注公众号的openId
            String wxUserId = msg.get("ToUserName"); // 这个是用户微信的id
            String msgType = msg.get("MsgType"); // 消息类型(event或者text)
            String createTime = msg.get("CreateTime"); // 消息创建时间 （整型）
            String ticket = msg.get("Ticket");

            if ("event".equals(msgType)) { // 如果是事件推送
                String eventType = msg.get("Event"); // 事件类型
                UserVO currentUser = new UserVO();
                ZooUsers zooUsers =null;
                String subscribeReturnXml ="";
                if ("subscribe".equals(eventType)) { // 如果是订阅消息
                    String subscribeContent = "感谢关注,成功登录动物园前线";
                    subscribeReturnXml = CommonToolUtils.getXmlString(msg, subscribeContent);
                    //查询用户，如果存在则添加进redis，如果不存在则入库并添加进redis
                    LambdaQueryWrapper<ZooUsers> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.select(ZooUsers::getId).eq(ZooUsers::getWx_openId, wxUserId);
                    ZooUsers users = usersMapper.selectOne(queryWrapper);
                    Integer UID = null;
                    //如果users为空则添加
                    if (Objects.isNull(users)) {
                        Long timeMillis = System.currentTimeMillis();
                        Timestamp currentDate = CommonToolUtils.getCurrentDate();
                        zooUsers = new ZooUsers();
                        zooUsers.setEmail("not Email");
                        zooUsers.setPassword("NULL");
                        zooUsers.setCreateTime(currentDate);
                        zooUsers.setUpdateTime(currentDate);
                        zooUsers.setName(timeMillis.toString());
                        zooUsers.setWx_openId(wxUserId);
                        UID = usersMapper.insert(zooUsers);
                    } else UID = users.getId();

                    zooUsers = usersMapper.selectById(UID);

                } else if ("SCAN".equals(eventType)) { // 如果是扫码消息
                    //从库里取出来放入redis,目前没有删除功能所以一定存在用户
                    LambdaQueryWrapper<ZooUsers> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.select(ZooUsers::getId).eq(ZooUsers::getWx_openId, wxUserId);
                    ZooUsers users = usersMapper.selectOne(queryWrapper);
                    zooUsers = usersMapper.selectById(users.getId());
                }
                redisTemplate.opsForValue().set(ticket, String.valueOf(zooUsers.getId()),10, TimeUnit.DAYS);
                return subscribeReturnXml;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public SseEmitter createSseConnect(String ticket) {
        SseEmitter sseEmitter = null;
        try {
            sseEmitter = new SseEmitterUtils().createSseEmitter(ticket);
            LocalCache.CACHE.put(ticket,sseEmitter);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return sseEmitter;
    }

    @Override
    public String wxQRCodeScan(String ticket) {
        String userId = redisTemplate.opsForValue().get(ticket);
        if(!StringUtils.isEmpty(userId)){
            ZooUsers zooUsers = usersMapper.selectById(Integer.valueOf(userId));
            UserVO currentUser = new UserVO();
            BeanUtils.copyProperties(zooUsers,currentUser);
            String jwt = JwtUtil.generateToken(currentUser);
            redisTemplate.opsForValue().set(UserConstant.USER_LOGIN_STATE + jwt+ UserConstant.USER_LOGIN_SUB_STATE, jwt, 7, TimeUnit.DAYS);
            return jwt;
        }
        //还有判断二维码过期操作
        return null;
    }


}
