package com.ygh.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ygh.domain.Base;
import com.ygh.domain.Chats;
import com.ygh.domain.Result;
import com.ygh.domain.User;
import com.ygh.service.ChatService;
import com.ygh.util.JwtUtil;

/**
 * @author ygh
 */
@RestController
@RequestMapping("/chat/history")
public class ChatHistoryController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatService chatService;
    
    @GetMapping("/personal")
    public Result getPersonalHistory(@RequestHeader("Token") String token, 
    @RequestParam("to_user_id") Long toId,
    @RequestParam("page_size") Integer pageSize,
    @RequestParam("page_num") Integer pageNum) throws IOException{

        Result result = new Result();
        String userInfo = jwtUtil.getUserInfo(token);
        User user = objectMapper.readValue(userInfo, User.class);
        Chats chats = chatService.searchPersonalHistory(Long.toString(user.getId()), Long.toString(toId), pageNum, pageSize);

        result.setData(chats);
        Base base = new Base();
        base.setCode(10000);
        base.setMsg("success");
        result.setBase(base);
        return result;
    }

    @GetMapping("/group")
    public Result getGroupHistory(@RequestHeader("Token") String token, 
    @RequestParam("group_id") Long groupId,
    @RequestParam("page_size") Integer pageSize,
    @RequestParam("page_num") Integer pageNum) throws IOException{

        Result result = new Result();
        String userInfo = jwtUtil.getUserInfo(token);
        User user = objectMapper.readValue(userInfo, User.class);
        Chats chats = chatService.searchGroupHistory(Long.toString(user.getId()), Long.toString(groupId), pageNum, pageSize);

        result.setData(chats);
        Base base = new Base();
        base.setCode(10000);
        base.setMsg("success");
        result.setBase(base);
        return result;
    }
}
