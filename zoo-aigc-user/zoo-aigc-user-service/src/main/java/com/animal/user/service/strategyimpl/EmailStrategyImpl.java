package com.animal.user.service.strategyimpl;

import com.animal.base.common.ValidatorCommon;
import com.animal.base.constant.UserConstant;
import com.animal.user.model.domain.ZooUsers;
import com.animal.user.model.dto.UserDTO;
import com.animal.user.strategy.UserStrategyInterface;
import jodd.util.StringUtil;
import org.springframework.util.DigestUtils;

/**
 * @author 咏鹅、AllianceTing
 * @version 1.0
 * @description TODO
 * @date 2023/7/19 23:19
 */
public class EmailStrategyImpl implements UserStrategyInterface {
    @Override
    public ZooUsers doEmailOrPhone(UserDTO userDTO, String type) {
        ValidatorCommon.userInfoIsValid(userDTO,type);
        ZooUsers user = new ZooUsers();

        if(!StringUtil.isEmpty(userDTO.getPassword())) {
            String entryPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + userDTO.getPassword()).getBytes());
            user.setPassword(entryPassword);
        }

        if(!StringUtil.isEmpty(userDTO.getEmail())){
            String email = userDTO.getEmail();
            user.setEmail(email);
        }

        if(!StringUtil.isEmpty(userDTO.getInvite_code())) {
            String invite_code = userDTO.getInvite_code();
            user.setInvite_code(invite_code);
        }

        return user;
    }



}
