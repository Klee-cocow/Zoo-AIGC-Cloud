package com.animal.user.service;

import com.animal.user.model.domain.ZooUsers;
import com.animal.user.model.request.UserLoginRequest;
import com.animal.user.model.request.UserRegisterRequest;
import com.animal.user.model.vo.UserVO;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author 咏鹅、AllianceTing
 * @description 针对表【zoo_users】的数据库操作Service
 * @createDate 2023-07-19 17:58:01
 */
public interface ZooUsersService extends IService<ZooUsers> {
    Integer userRegister(UserRegisterRequest userRegisterRequest, String registerIdentity, HttpServletRequest request);

    String userLogin(UserLoginRequest userLoginRequest, String loginIdentity, HttpServletRequest request);

    UserVO getLoginUser(HttpServletRequest request);

    String generateCodeToEmail(String email);

    Boolean logoutUser(HttpServletRequest request);
}
