package com.animal.user.service.impl;

import cn.hutool.Hutool;
import cn.hutool.core.util.RandomUtil;
import com.animal.base.common.ErrorCode;
import com.animal.base.constant.IdentityEnum;
import com.animal.base.constant.UserConstant;
import com.animal.base.exception.BusinessException;
import com.animal.base.utils.CommonToolUtils;
import com.animal.base.utils.JwtUtil;
import com.animal.user.mapper.ZooUsersMapper;
import com.animal.user.model.domain.ZooUsers;
import com.animal.user.model.dto.MailSenderDTO;
import com.animal.user.model.dto.UserDTO;
import com.animal.user.model.request.UserLoginRequest;
import com.animal.user.model.request.UserRegisterRequest;
import com.animal.user.model.vo.UserVO;
import com.animal.user.service.ZooUsersService;
import com.animal.user.strategy.UserStrategyContent;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.xml.messaging.saaj.packaging.mime.MessagingException;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import net.sf.jsqlparser.statement.select.KSQLJoinWindow;
import net.sf.jsqlparser.statement.select.KSQLWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


/**
 * @author 咏鹅、AllianceTing
 * @description 针对表【zoo_users】的数据库操作Service实现
 * @createDate 2023-07-19 17:58:01
 */
@Service
public class ZooUsersServiceImpl extends ServiceImpl<ZooUsersMapper, ZooUsers>
        implements ZooUsersService {

    //邮箱校验
    public final String validPattern = ".*[[ _`=|\\[\\]~……——+|{}‘]|\\n|\\r|\\t].*";

    private final Logger log = LoggerFactory.getLogger("ZooUserService");

    @Resource
    private MailSenderDTO mailSenderDTO;

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private ZooUsersMapper zooUsersMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private SpringTemplateEngine templateEngin;

    @Override
    public Integer userRegister(UserRegisterRequest userRegisterRequest, String registerIdentity, HttpServletRequest request) {

        //查找用户是否已经注册
        String res = redisTemplate.opsForValue().get(userRegisterRequest.getEmail());
        if (res != null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "当前账户已存在");
        }
        QueryWrapper<ZooUsers> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", userRegisterRequest.getEmail());
        long count = zooUsersMapper.selectCount(queryWrapper);
        if (count > 0) {
            int userOut = RandomUtil.randomInt(1, 9);
            redisTemplate.opsForValue().set(userRegisterRequest.getEmail(), "1", userOut, TimeUnit.MINUTES);
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "当前账户已存在");
        }

        //判断验证码
        String invite_code = userRegisterRequest.getInvite_code();
        String code = redisTemplate.opsForValue().get(UserConstant.USER_LOGIN_STATE + userRegisterRequest.getEmail());
        if (code == null) {
            throw new BusinessException(ErrorCode.NO_QUERY, "邮箱验证码不存在");
        }

        if (invite_code == null || (code == null && invite_code == null) || !invite_code.equals(code)) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "请输入正确的邮箱验证码");
        }

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userRegisterRequest, userDTO);
        //统一处理 邮箱注册和手机号注册的情况
        ZooUsers user = UserStrategyContent.doUserRegister(IdentityEnum.valueOf(registerIdentity)).doEmailOrPhone(userDTO, registerIdentity);

        //获取用户ip
        String ipAddr = CommonToolUtils.getIpAddr(request);
        Long timeMillis = System.currentTimeMillis();
        Timestamp currentDate = CommonToolUtils.getCurrentDate();
        user.setIp(ipAddr);
        user.setUpdateTime(currentDate);
        user.setCreateTime(currentDate);
        user.setName(timeMillis.toString());
        user.setDescription(UserConstant.USER_DESCRIPTION);  //默认设置

        try {
            this.save(user);
        } catch (Exception e) {

            log.error("保存用户失败了 时间：" + currentDate, user);
            throw new BusinessException(ErrorCode.NO_SAVE);
        }

        return user.getId();
    }

    @Override
    public String userLogin(UserLoginRequest userLoginRequest, String loginIdentity, HttpServletRequest request) {

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userLoginRequest, userDTO);
        //统一处理 邮箱注册和手机号注册的情况
        ZooUsers user = UserStrategyContent.doUserLogin(IdentityEnum.valueOf(loginIdentity)).doEmailOrPhone(userDTO, loginIdentity);


        QueryWrapper<ZooUsers> queryWrapper = new QueryWrapper<>();

        if (user.getEmail() != null)
            queryWrapper.eq("email", userLoginRequest.getEmail());

        if (user.getPhone() != null) {
            queryWrapper.eq("phone", userLoginRequest.getEmail());
        }

        String phone_code = user.getPhone_code();
        //如果是手机登录，拿到手机验证码，然后去查询是否对应上了 如果正确对应则放行

        if (user.getPassword() != null) {
            queryWrapper.eq("password", user.getPassword());
        }


        user = zooUsersMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.NO_QUERY, "此用户不存在");
        }
        UserVO userResult = new UserVO();

        //赋值传递脱敏
        BeanUtils.copyProperties(user, userResult);
        //登录成功后设置jwt令牌
        String token = JwtUtil.generateToken(userResult);
        redisTemplate.opsForValue().set(UserConstant.USER_LOGIN_STATE + token + UserConstant.USER_LOGIN_SUB_STATE, token, 7, TimeUnit.SECONDS);


        return token;
    }

    @Override
    public UserVO getLoginUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN, "没有登录");
        }

        String t = redisTemplate.opsForValue().get(UserConstant.USER_LOGIN_STATE + token + UserConstant.USER_LOGIN_SUB_STATE);
        if (!token.equals(t)) {
            throw new BusinessException(ErrorCode.NO_LOGIN, "没有登录");
        }

        Boolean flag = JwtUtil.checkToken(t);
        if (!flag) {
            throw new BusinessException(ErrorCode.NO_AUTH, "token过期");
        }


        DecodedJWT userJwt = JwtUtil.getToken(token);
        UserVO user = new UserVO();
        String name = userJwt.getClaim("Name").asString();
        String phone = userJwt.getClaim("Phone").asString();
        String email = userJwt.getClaim("Email").asString();
        String avatar = userJwt.getClaim("Avatar").asString();
        String money = userJwt.getClaim("Money").asString();
        String description = userJwt.getClaim("Description").asString();
        Integer id = userJwt.getClaim("Id").asInt();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);
        user.setAvatar(avatar);
        user.setRemember_token(null);  //暂无邀请码
        user.setPhone(phone);
        user.setDescription(description);
        BigDecimal moneyDecimal = new BigDecimal(money);
        user.setMoney(moneyDecimal);
        return user;
    }

    @Override
    //生成验证码发往用户邮箱
    public String generateCodeToEmail(String email) {
        //校验邮箱是否合法
        if (email == null) throw new BusinessException(ErrorCode.PARAMETER_ERROR, "邮箱不合法");
        //redis中查询是否已经有这个邮箱存在
        String judgeCode = redisTemplate.opsForValue().get(UserConstant.USER_LOGIN_STATE + email);
        if (!(judgeCode == null)) {
            return judgeCode;
        }
        //避免恶意调用
        String res = redisTemplate.opsForValue().get(email);
        if (res != null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "当前账户已存在");
        }

        //查找用户是否已经注册
        QueryWrapper<ZooUsers> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        long count = zooUsersMapper.selectCount(queryWrapper);
        if (count > 0) {
            int userOut = RandomUtil.randomInt(1, 9);
            redisTemplate.opsForValue().set(email, "1", userOut, TimeUnit.MINUTES);
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "当前账户已存在");
        }

        String code = RandomUtil.randomNumbers(4);
        Context context = new Context();
        context.setVariable("EmailCode", Arrays.asList(code.split("")));
        redisTemplate.opsForValue().set(UserConstant.USER_LOGIN_STATE + email, code, 5, TimeUnit.MINUTES);
        try {
            this.sendMail(email,context);
        } catch (jakarta.mail.MessagingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "发送邮件失败");
        }

        return code;
    }

    @Override
    public Boolean logoutUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        redisTemplate.opsForValue().set(UserConstant.USER_LOGIN_STATE + token + UserConstant.USER_LOGIN_SUB_STATE, "", 30, TimeUnit.MINUTES);
        return true;
    }

    //发送邮箱
    public void sendMail(String mailDes,Context context) throws jakarta.mail.MessagingException {
        mailSenderDTO.setToEmail(mailDes);
        String sendEmailToUser = templateEngin.process("SendEmailToUser", context);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        messageHelper.setFrom(mailSenderDTO.getEmailFrom());
        messageHelper.setSubject(mailSenderDTO.getSubject());
        messageHelper.setTo(mailSenderDTO.getToEmail());
        messageHelper.setText(sendEmailToUser,true);

        javaMailSender.send(mimeMessage);
    }


}




