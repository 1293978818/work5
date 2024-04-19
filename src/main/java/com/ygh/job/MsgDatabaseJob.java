package com.ygh.job;

import java.io.IOException;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
public class MsgDatabaseJob extends QuartzJobBean implements ApplicationContextAware{

    private StringRedisTemplate stringRedisTemplate;

    private ChatMapper chatMapper;

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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.stringRedisTemplate = applicationContext.getBean(StringRedisTemplate.class);
        this.chatMapper = applicationContext.getBean(ChatMapper.class);
        this.objectMapper = applicationContext.getBean(ObjectMapper.class);
    }
    
}
