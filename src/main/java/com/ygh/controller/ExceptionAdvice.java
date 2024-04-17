package com.ygh.controller;

import java.io.StringWriter;
import java.io.PrintWriter;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ygh.domain.Base;
import com.ygh.domain.Result;
import com.ygh.exception.BizException;

import lombok.extern.slf4j.Slf4j;

/**
 * 异常处理类
 * @author ygh
 */

 @RestControllerAdvice
 @Slf4j
 public class ExceptionAdvice {
     
     @ExceptionHandler(BizException.class)
     public Result doBizException(BizException bizException){
         Result result = new Result();
         result.setBase(new Base(-1, bizException.getMessage()));
         return result;
     }
 
     @ExceptionHandler(Exception.class)
     public Result unknownException(Exception exception){
         Result result = new Result();
         result.setBase(new Base(-1, exception.getMessage()));
         
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         exception.printStackTrace(pw);
         String stackTrace = sw.toString();
 
         log.error(exception.getMessage());
         log.error("Stack trace: " + stackTrace);
         return result;
     }
 }
