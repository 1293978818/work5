package com.ygh.service;

/**
 * 用户服务接口
 * @author ygh
 */
public interface UserService {
    
    /**
     * 注册
     * @param username
     * @param password
     */
    public void insertUser(String username,String password);
}
