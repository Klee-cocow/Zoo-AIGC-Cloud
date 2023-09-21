package com.animal.base.common;

import com.animal.base.constant.IdentityEnum;
import com.animal.base.exception.BusinessException;
import com.animal.user.model.dto.UserDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @author AllianceTing、咏鹅
 * @version 1.0
 * @description 校验
 * @date 2023/7/19 23:27
 */
public class ValidatorCommon {

    public static void userInfoIsValid(UserDTO user, String type) {



        //手机号判断
        if(type.equals(IdentityEnum.phone.toString())){
            if (!Pattern.compile("^[1-9]{6,16}$").matcher(user.getPhone()).matches()) {
                throw new BusinessException(ErrorCode.PARAMETER_ERROR, "userRegistryRequest.User Phone Parms error");
            }

        }
        //匹配邮箱
        if(type.equals(IdentityEnum.email.toString())){
            String email = user.getEmail();
            String password = user.getPassword();

            if (StringUtils.isAnyBlank(password,email)) {
                throw new BusinessException(ErrorCode.PARAMETER_ERROR, "邮箱或密码不能为空");
            }
            if (!Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$").matcher(email).matches()) {
                throw new BusinessException(ErrorCode.PARAMETER_ERROR, "userRegistryRequest.User MailID Parms error");
            }
            if (password.length() < 6) {
                throw new BusinessException(ErrorCode.PARAMETER_ERROR, "密码长度必须大于等于6位");
            }
        }
    }
}
