package com.ygh.job;

import java.io.IOException;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ygh.domain.Chat;
import com.ygh.exception.BizException;
import com.ygh.mapper.ChatMapper;

/**
 * 消息存储定时任务
 * @author ygh
 */
public class MsgDatabaseJob extends QuartzJobBean{

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        
        stringRedisTemplate.opsForList().range("chat", 0, -1).forEach(str -> {
            Chat chat = null;

            try {
                chat = objectMapper.readValue(str, Chat.class);
            } catch (IOException e) {
                throw new BizException("消息序列化失败");
            }

            chatMapper.insert(chat);
        });
    }
    
}
