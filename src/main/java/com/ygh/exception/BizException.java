package com.ygh.exception;

/**
 * 业务异常类
 * @author ygh
 */
public class BizException extends RuntimeException{
    
    public BizException(String msg){
        super(msg);
    }
}
