package com.ygh.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.ygh.domain.User;
import com.ygh.exception.BizException;
import com.ygh.mapper.UserMapper;
import com.ygh.service.FriendService;

/**
 * 好友服务实现类
 * @author ygh
 */
@Service
public class FriendServiceImpl implements FriendService{

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void addFriend(Long userId, Long friendId) {

        if(userId.equals(friendId)){
            throw new BizException("联系人不能是自己");
        }

        User user = userMapper.selectById(friendId);

        if(user == null){
            throw new BizException("该联系人不存在");
        }
        
        List<String> friends = stringRedisTemplate.opsForList().range("friend:" + userId, 0, -1);
        if(friends != null && friends.contains(Long.toString(friendId))){
            throw new BizException("联系人已经在你的联系人列表中");
        }

        stringRedisTemplate.opsForList().leftPush("friend:" + userId, Long.toString(friendId));
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        
        if(userId.equals(friendId)){
            throw new BizException("联系人不能是自己");
        }

        List<String> friends = stringRedisTemplate.opsForList().range("friend:" + userId, 0, -1);
        if(friends == null || !friends.contains(Long.toString(friendId))){
            throw new BizException("联系人不在你的联系人列表中");
        }

        stringRedisTemplate.opsForList().remove("friend:" + userId, 1, Long.toString(friendId));
    }

    @Override
    public List<User> selectFriend(Long userId, Integer pageSize, Integer pageNum) {
        pageNum --;

        if(pageSize < 0 || pageNum < 0){
            throw new BizException("页码信息不正确");
        }

        List<User> users = new ArrayList<>();
        
        List<String> friends = stringRedisTemplate.opsForList().range("friend:" + userId, pageNum * pageSize, (pageNum + 1) * pageSize - 1);

        for(String friendId : friends){
            User user = userMapper.selectById(friendId);
            users.add(user);
        }

        return users;
    }

    
    
}
