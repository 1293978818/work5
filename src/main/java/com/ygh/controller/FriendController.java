package com.ygh.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ygh.domain.Base;
import com.ygh.domain.Result;
import com.ygh.domain.User;
import com.ygh.service.FriendService;
import com.ygh.util.JwtUtil;

/**
 * 联系人控制器
 * @author ygh
 */
@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FriendService friendService;
    
    @PostMapping("/add")
    public Result addFriend(@RequestHeader("Token") String token, @RequestParam("friend_id") Long friendId) throws JsonMappingException, JsonProcessingException{
        Result result = new Result();
        Base base = new Base();

        String userInfo = jwtUtil.getUserInfo(token);
        User user = objectMapper.readValue(userInfo, User.class);

        friendService.addFriend(user.getId(), friendId);

        base.setCode(10000);
        base.setMsg("success");
        result.setBase(base);
        return result;
    }

    @DeleteMapping("/delete")
    public Result deleteFriend(@RequestHeader("Token") String token, @RequestParam("friend_id") Long friendId) throws JsonMappingException, JsonProcessingException{
        Result result = new Result();
        Base base = new Base();

        String userInfo = jwtUtil.getUserInfo(token);
        User user = objectMapper.readValue(userInfo, User.class);

        friendService.deleteFriend(user.getId(), friendId);

        base.setCode(10000);
        base.setMsg("success");
        result.setBase(base);
        return result;
    }

    @GetMapping("/select")
    public Result selectFriend(@RequestHeader("Token") String token, @RequestParam("page_size") Integer pageSize, @RequestParam("page_num") Integer pageNum) throws JsonMappingException, JsonProcessingException{

        Result result = new Result();
        Base base = new Base();

        String userInfo = jwtUtil.getUserInfo(token);
        User user = objectMapper.readValue(userInfo, User.class);

        List<User> users = friendService.selectFriend(user.getId(), pageSize, pageNum);

        base.setCode(10000);
        base.setMsg("success");
        result.setData(users);
        result.setBase(base);
        return result;
    }
}
