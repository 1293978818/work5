package com.ygh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ygh.domain.Base;
import com.ygh.domain.Group;
import com.ygh.domain.Result;
import com.ygh.domain.User;
import com.ygh.service.GroupService;
import com.ygh.util.JwtUtil;

/**
 * 群组控制器
 * @author ygh
 */
@RestController
@RequestMapping("/group")
public class GroupController {
    
    @Autowired
    private GroupService groupService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;
    
    @PostMapping("/create")
    public Result createGroup(@RequestHeader("Token") String token) throws JsonMappingException, JsonProcessingException{
        Result result = new Result();
        Base base = new Base();

        String userInfo = jwtUtil.getUserInfo(token);
        User user = objectMapper.readValue(userInfo,User.class);

        Group group = groupService.createGroup(user.getId());

        base.setCode(10000);
        base.setMsg("success");
        result.setBase(base);
        result.setData(group);
        return result;
    }

    @PostMapping("/join")
    public Result joinGroup(@RequestHeader("token") String accessToken,
    @RequestParam("group_id") Long groupId) throws JsonMappingException, JsonProcessingException{
        Result result = new Result();
        Base base = new Base();
        
        String userInfo = jwtUtil.getUserInfo(accessToken);
        User user = objectMapper.readValue(userInfo, User.class);

        groupService.joinGroup(user.getId(), groupId);

        base.setCode(10000);
        base.setMsg("success");
        result.setBase(base);
        return result;
    }
}
