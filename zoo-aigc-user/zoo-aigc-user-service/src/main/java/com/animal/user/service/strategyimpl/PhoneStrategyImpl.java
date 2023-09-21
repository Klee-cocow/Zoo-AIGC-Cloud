package com.animal.user.service.strategyimpl;

import com.animal.base.common.ValidatorCommon;
import com.animal.user.model.domain.ZooUsers;
import com.animal.user.model.dto.UserDTO;
import com.animal.user.strategy.UserStrategyInterface;
import jodd.util.StringUtil;

/**
 * @author 咏鹅、AllianceTing
 * @version 1.0
 * @description TODO
 * @date 2023/7/19 23:20
 */
public class PhoneStrategyImpl implements UserStrategyInterface {
    @Override
    public ZooUsers doEmailOrPhone(UserDTO userDTO, String type) {
        ValidatorCommon.userInfoIsValid(userDTO,type);
        ZooUsers user = new ZooUsers();

        if(!StringUtil.isEmpty(userDTO.getPhone())){
            String phone = userDTO.getPhone();
            user.setPhone(phone);
        }

        if(!StringUtil.isEmpty(userDTO.getPhone_code())){
            String phone_code = userDTO.getPhone_code();
            user.setPhone_code(phone_code);
        }

        return user;
    }
}
