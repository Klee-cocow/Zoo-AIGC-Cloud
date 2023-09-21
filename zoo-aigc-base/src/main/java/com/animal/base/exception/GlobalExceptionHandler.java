package com.animal.base.exception;


import com.animal.base.common.ErrorCode;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 *
 * @author <a href="Alliance github_https://github.com/AllianceTing"/>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 处理业务异常BusinessException
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleMyBusinessException(BusinessException ex) {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String chromeVersion = request.getHeader("User-Agent");
        Map<String, Object> result = new HashMap<>();
        result.put("errCode", ex.getCode());
        result.put("errMsg", ex.getMessage());
        result.put("errDescription",ex.getDescription());
        result.put("nowTime", LocalDateTime.now(Clock.systemDefaultZone()));
        result.put("chromeVersion", chromeVersion);
        log.info(result.toString());
        return result;
    }

    // 处理常见的数组异常ArrayIndexOutOfBoundsException
    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public Map<String, Object> handleArraysException(ArrayIndexOutOfBoundsException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("errCode", ErrorCode.OUT_OF_BOUNDS);
        result.put("errMsg", "index Out of Bounds");
        result.put("nowTime", LocalDateTime.now(Clock.systemDefaultZone()));
        log.info("ArrayIndexOutOfBoundsException====>" + ex.getMessage());
        return result;
    }

    // 处理常见的栈异常StackOverflowError
    @ExceptionHandler(StackOverflowError.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleStackException(StackOverflowError ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("errCode", ErrorCode.OVER_FLOW);
        result.put("errMsg", "warning internal exception");
        result.put("nowTime", LocalDateTime.now(Clock.systemDefaultZone()));
        log.error("StackOverflowError====>" + ex.getMessage());
        return result;
    }

    // 处理常见的空指针异常NullPointerException
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleNPEException(NullPointerException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("errCode", ex.getLocalizedMessage());
        result.put("errMsg", ex.getMessage());
        result.put("nowTime", LocalDateTime.now(Clock.systemDefaultZone()));
        log.error(result.toString());
        return result;
    }

    // 处理常见的数据库异常DataBaseException
    @ExceptionHandler(SQLException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleSQLException(SQLException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("errCode", ex.getErrorCode());
        result.put("errMsg", "internal error for db");
        result.put("nowTime", LocalDateTime.now(Clock.systemDefaultZone()));
        log.warn("DataBaseException=====>" + ex.getMessage());
        return result;
    }

    // 处理DAO异常MybatisPlusException
    @ExceptionHandler(MybatisPlusException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleMyBatisPlusException(MybatisPlusException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("errCode", ErrorCode.SYSTEM_ERROR);
        result.put("errMsg", "internal error for dao frame work");
        result.put("nowTime", LocalDateTime.now(Clock.systemDefaultZone()));
        log.error("MybatisPlusException=====>" + ex.getMessage());
        return result;
    }
}
