package com.ygh.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ygh.domain.Base;
import com.ygh.domain.User;
import com.ygh.exception.BizException;
import com.ygh.mapper.UserMapper;
import com.ygh.service.ChatService;
import com.ygh.util.JwtUtil;

import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

/**
 * 聊天控制器
 * @author ygh
 */
@Component
@ServerEndpoint(value = "/chat/online")
@Slf4j
public class ChatController {
    
    private static UserMapper userMapper;

    private static JwtUtil jwtUtil;

    private static ObjectMapper objectMapper;

    private static ChatService chatService;

    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil){
        ChatController.jwtUtil = jwtUtil;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper){
        ChatController.userMapper = userMapper;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper){
        ChatController.objectMapper = objectMapper;
    }

    @Autowired
    public void getChatService(ChatService chatService){
        ChatController.chatService = chatService;
    }

    @OnOpen
    public void onOpen(Session session) throws Exception{
        log.info("onopen");

        Map<String,List<String>> requestParameterMap = session.getRequestParameterMap();
        List<String> token = requestParameterMap.get("token");

        if(token == null){
            log.info("用户未登录");
            throw new BizException("用户未登录");
        }

        if(!jwtUtil.verifyJwt(token.get(0))){
            log.info("用户名或密码错误");
            throw new BizException("用户名或密码错误");
        }

        String userInfo = jwtUtil.getUserInfo(token.get(0));

        User user = objectMapper.readValue(userInfo, User.class);

        User user2 = userMapper.selectById(user.getId());

        if(user2 == null){
            log.info("用户信息不存在");
            throw new BizException("用户信息不存在");
        }

        chatService.addSession(session, Long.toString(user.getId()));
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) throws IOException {
        chatService.removeSession(session);
    }

    @OnMessage
    public void onMessage(Session session,String message) throws IOException{

        Map<String,List<String>> requestParameterMap = session.getRequestParameterMap();
        List<String> token = requestParameterMap.get("token");
        String userInfo = jwtUtil.getUserInfo(token.get(0));
        User user = objectMapper.readValue(userInfo, User.class);
        String type = requestParameterMap.get("type").get(0);

        switch(type){
            case "1":
                String toUserId = requestParameterMap.get("to_user_id").get(0);
                chatService.onOneChat(session,Long.toString(user.getId()), toUserId, message);
                break;
            case "2":
                String groupId = requestParameterMap.get("group_id").get(0);
                chatService.chatInGroup(session, Long.toString(user.getId()), groupId, message);
                break;
            default:
                throw new BizException("未找到该格式");
        }
        
    }

    @OnError
    public void onError(Session session,Throwable throwable) throws IOException{
        Base base = new Base();
        base.setCode(-1);
        base.setMsg(throwable.getMessage());

        log.error(null, throwable);
        String message = objectMapper.writeValueAsString(base);

        session.getBasicRemote().sendText(message);
    }
}
