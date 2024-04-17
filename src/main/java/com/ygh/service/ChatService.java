package com.ygh.service;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.websocket.Session;

/**
 * 聊天服务接口
 * @author ygh
 */
public interface ChatService {
    
    /**
     * 添加session到集合中
     * @param session
     * @param userId
     */
    public void addSession(Session session,String userId);

    /**
     * 私聊发送消息
     * @param session
     * @param userId
     * @param toUserId
     * @param message
     * @throws JsonProcessingException 
     * @throws IOException 
     */
    public void onOneChat(Session session,String userId,String toUserId,String message) throws JsonProcessingException, IOException;

    /**
     * 移除对应的session
     * @param session
     * @throws IOException 
     */
    public void removeSession(Session session) throws IOException;

    /**
     * 查询聊天记录
     * @param session
     * @param userId
     * @param toUserId
     * @param pageNum
     * @param pageSize
     * @throws JsonProcessingException
     * @throws IOException
     */
    public void searchPersonalHistory(Session session,String userId,String toUserId,Integer pageNum,Integer pageSize) throws JsonProcessingException, IOException;

    /**
     * 发送群聊消息
     * @param session
     * @param userId
     * @param groupId
     * @param message
     * @throws IOException 
     */
    public void chatInGroup(Session session,String userId,String groupId,String message) throws IOException;

    /**
     * 查询群聊历史消息
     * @param session
     * @param userId
     * @param groupId
     * @param pageSize
     * @param pageNum
     * @throws IOException 
     */
    public void searchGroupHistory(Session session,String userId,String groupId,Integer pageSize,Integer pageNum) throws IOException;
}
