package com.animal.user.controller;

import com.animal.base.common.BaseResponse;
import com.animal.base.common.ErrorCode;
import com.animal.base.exception.BusinessException;
import com.animal.base.utils.ResultUtil;
import com.animal.user.model.request.UserLoginRequest;
import com.animal.user.model.request.UserRegisterRequest;
import com.animal.user.model.vo.UserVO;
import com.animal.user.service.ZooUsersService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * @author 咏鹅、AllianceTing
 * @version 1.0
 * @description 用户方法
 * @date 2023/7/19 18:01
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private ZooUsersService userService;


    @PostMapping("/register")
    public BaseResponse<Integer> userRegister(@RequestBody UserRegisterRequest userRegisterRequest, HttpServletRequest request) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        String registerIdentity = userRegisterRequest.getRegisterIdentity();
        Integer result = userService.userRegister(userRegisterRequest, registerIdentity, request);

        return ResultUtil.success(result);
    }


    @PostMapping("/login")
    public BaseResponse<String> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        String loginIdentity = userLoginRequest.getLoginIdentity();
        String user = userService.userLogin(userLoginRequest, loginIdentity, request);


        return ResultUtil.success(user);
    }

    @PostMapping("/sendcode")
    public void sendCode(@RequestParam("email") String email) {
        userService.generateCodeToEmail(email);
    }


    @GetMapping("/getLoginUser")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {

        UserVO loginUser = userService.getLoginUser(request);
        //验证token有效性
        return ResultUtil.success(loginUser);
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> logoutUser(HttpServletRequest request) {
        Boolean flag = userService.logoutUser(request);
        return ResultUtil.success(flag);
    }


}
