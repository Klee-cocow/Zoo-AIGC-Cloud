package com.animal.user.strategy;

import com.animal.base.common.ErrorCode;
import com.animal.base.constant.IdentityEnum;
import com.animal.base.exception.BusinessException;
import com.animal.user.service.strategyimpl.EmailStrategyImpl;
import com.animal.user.service.strategyimpl.PhoneStrategyImpl;

import java.util.HashMap;

/**
 * @author 咏鹅、AllianceTing
 * @version 1.0
 * @description TODO
 * @date 2023/7/19 22:51
 */
public class UserStrategyContent {


    private static final HashMap<IdentityEnum,UserStrategyInterface> emailOrPhoneStrategies = new HashMap<>();

    static{
        emailOrPhoneStrategies.put(IdentityEnum.email, new EmailStrategyImpl());
        emailOrPhoneStrategies.put(IdentityEnum.phone,new PhoneStrategyImpl());
    }

    public static UserStrategyInterface doUserLogin(IdentityEnum loginIdentity){
        if(loginIdentity == null) throw new BusinessException(ErrorCode.PARAMETER_ERROR,"未传输登录识别码");
        if(!emailOrPhoneStrategies.containsKey(loginIdentity)) throw new BusinessException(ErrorCode.PARAMETER_ERROR,"此登录识别码不存在");
        return emailOrPhoneStrategies.get(loginIdentity);
    }

    public static UserStrategyInterface doUserRegister(IdentityEnum registerIdentity){
        if(registerIdentity == null) throw new BusinessException(ErrorCode.PARAMETER_ERROR,"未传输注册识别码");
        if(!emailOrPhoneStrategies.containsKey(registerIdentity)) throw new BusinessException(ErrorCode.PARAMETER_ERROR,"此注册识别码不存在");
        return emailOrPhoneStrategies.get(registerIdentity);
    }

}
