package com.ygh.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.ygh.domain.Group;
import com.ygh.domain.User;
import com.ygh.exception.BizException;
import com.ygh.mapper.GroupMapper;
import com.ygh.mapper.UserMapper;
import com.ygh.service.GroupService;

import java.util.List;

/**
 * 群聊服务实现类
 * @author ygh
 */
@Service
public class GroupServiceImpl implements GroupService{
    
    @Autowired
    private IdentifierGenerator identifierGenerator;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Group createGroup(Long userId) {
        
        User user = userMapper.selectById(userId);

        if(user == null){
            throw new BizException("该用户不存在");
        }

        Group group = new Group();
        Long id = identifierGenerator.nextId(0).longValue();
        group.setGroupId(id);
        group.setMemberCount(1);

        stringRedisTemplate.opsForList().leftPush("group:" + id, Long.toString(userId));
        groupMapper.insert(group);

        return group;
    }

    @Override
    public void joinGroup(Long userId, Long groupId) {
        
        User user = userMapper.selectById(userId);

        if(user == null){
            throw new BizException("该用户不存在");
        }

        Group group = groupMapper.selectById(groupId);

        if(group == null){
            throw new BizException("群聊不存在");
        }

        List<String> allMembers = stringRedisTemplate.opsForList().range("group:" + group.getGroupId(), 0, -1);
        if(allMembers.contains(Long.toString(userId))){
            throw new BizException("该用户已经在群聊中");
        }

        group.setMemberCount(group.getMemberCount() + 1);
        stringRedisTemplate.opsForList().leftPush("group:" + group.getGroupId(),Long.toString(userId));
        groupMapper.updateById(group);
    }
}
