package com.animal.user.strategy;

import com.animal.user.model.domain.ZooUsers;
import com.animal.user.model.dto.UserDTO;

/**
 * @author 咏鹅、AllianceTing
 * @version 1.0
 * @description TODO
 * @date 2023/7/19 22:52
 */
public interface UserStrategyInterface {

    ZooUsers doEmailOrPhone(UserDTO users, String type);
}
