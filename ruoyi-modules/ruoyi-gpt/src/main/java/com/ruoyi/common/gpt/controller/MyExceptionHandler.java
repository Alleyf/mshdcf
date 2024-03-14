package com.ruoyi.common.gpt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author csFan
 * @version 1.0
 * @description: TODO
 * @date 2024/3/14 16:09
 */
@ControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<String> handleException(Exception ex) {
        // 这里可以自定义对所有异常的处理逻辑，例如记录日志、返回友好的错误信息等
        String errorMessage = "An unexpected error occurred: " + ex.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 可以添加更多的异常处理器方法来处理特定类型的异常
    @ExceptionHandler(value = {NullPointerException.class})
    public ResponseEntity<String> handleNullPointerException(NullPointerException ex) {
        String errorMessage = "Null Pointer Exception: " + ex.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
