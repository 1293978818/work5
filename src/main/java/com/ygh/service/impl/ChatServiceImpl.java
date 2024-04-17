package com.ygh.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ygh.domain.Chat;
import com.ygh.domain.Chats;
import com.ygh.domain.Group;
import com.ygh.domain.User;
import com.ygh.exception.BizException;
import com.ygh.mapper.ChatMapper;
import com.ygh.mapper.GroupMapper;
import com.ygh.mapper.UserMapper;
import com.ygh.service.ChatService;

import jakarta.websocket.Session;

/**
 * 聊天服务实现类
 * @author ygh
 */
@Service
public class ChatServiceImpl implements ChatService{
    
    private ConcurrentHashMap<String,Session> concurrentHashMap = new ConcurrentHashMap<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addSession(Session session,String userId) {
        concurrentHashMap.put(userId, session);
    }

    @Override
    public void onOneChat(Session session, String userId, String toUserId, String message) throws IOException {

        if(!isIdRight(toUserId)){
            throw new BizException("发起聊天的对象不存在");
        }

        Session toSession = concurrentHashMap.get(toUserId);

        Chat chat = new Chat();
        chat.setContent(message);
        chat.setFromId(userId);
        chat.setToUserId(toUserId);
        chat.setTime(new Timestamp(System.currentTimeMillis()));

        String type = session.getRequestParameterMap().get("type").get(0);
        String result = objectMapper.writeValueAsString(chat);
        boolean flag = "1".equals(type);
        if(toSession == null || (!flag)){
            stringRedisTemplate.opsForList().leftPush("chat:fromId:" + userId + ":toId:" + toUserId, result);
            return;
        }
        
        
        stringRedisTemplate.opsForList().leftPush("chat:fromId:" + userId + ":toId:" + toUserId, result);
        toSession.getBasicRemote().sendText(result);

    }

    @Override
    public void removeSession(Session session) throws IOException {
        if (session.isOpen()) {
            session.close();
        }
        concurrentHashMap.entrySet().removeIf(entry -> {
            if (entry.getValue().equals(session)) {
                try {
                    entry.getValue().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public void searchPersonalHistory(Session session, String userId, String toUserId, Integer pageNum,
            Integer pageSize) throws IOException {
        pageNum --;
        List<String> list1 = stringRedisTemplate.opsForList().range("chat:fromId:" + userId + ":toId:" + toUserId, 0, -1);
        List<String> list2 = stringRedisTemplate.opsForList().range("chat:fromId:" + toUserId + ":toId:" + userId, 0, -1);

        Chats chats = new Chats();

        list1.addAll(list2);
        list1.sort((o1, o2) -> {
            Chat chat1 = null;
            Chat chat2 = null;
            try {
                chat1 = objectMapper.readValue(o1, Chat.class);
                chat2 = objectMapper.readValue(o2, Chat.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return chat1.getTime().compareTo(chat2.getTime()) * -1;
        });

        List<Chat> resultList = new ArrayList<>();
        if(list1.size() < pageSize * pageNum + 1){
            
            LambdaQueryWrapper<Chat> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Chat::getFromId, userId);
            lambdaQueryWrapper.eq(Chat::getToUserId, toUserId);
            lambdaQueryWrapper.or();
            lambdaQueryWrapper.eq(Chat::getFromId, toUserId);
            lambdaQueryWrapper.eq(Chat::getToUserId, userId);
            lambdaQueryWrapper.orderBy(true, false, Chat::getTime);

            lambdaQueryWrapper.last("limit " + (pageSize * pageNum - list1.size()) + ", " + pageSize);
            resultList = chatMapper.selectList(lambdaQueryWrapper);

        }else if(list1.size() >= pageSize * pageNum + pageSize){

            List<String> list = list1.subList(pageSize * pageNum, pageSize * pageNum + pageSize);
            for(String str : list){
                Chat chat = objectMapper.readValue(str, Chat.class);
                resultList.add(chat);
            }
        }else{

            for(int i = pageSize * pageNum; i < list1.size(); i++){
                Chat chat = objectMapper.readValue(list1.get(i), Chat.class);
                resultList.add(chat);
            }

            int already = resultList.size();
            LambdaQueryWrapper<Chat> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Chat::getFromId, userId);
            lambdaQueryWrapper.eq(Chat::getToUserId, toUserId);
            lambdaQueryWrapper.or();
            lambdaQueryWrapper.eq(Chat::getFromId, toUserId);
            lambdaQueryWrapper.eq(Chat::getToUserId, userId);
            lambdaQueryWrapper.orderBy(true, false, Chat::getTime);

            lambdaQueryWrapper.last("limit " + 0 + ", " + (pageSize - already));
            List<Chat> list = chatMapper.selectList(lambdaQueryWrapper);
            resultList.addAll(list);
        }

        resultList.sort((o1, o2) -> o1.getTime().compareTo(o2.getTime()) * -1);
        chats.setChats(resultList);
        chats.setTotal((long) resultList.size());

        String result = objectMapper.writeValueAsString(chats);
        session.getBasicRemote().sendText(result);
        removeSession(session);
    }

    private boolean isIdRight(String id){
        User user = userMapper.selectById(id);
        return user != null;
    }

    @Override
    public void chatInGroup(Session session, String userId, String groupId, String message) throws IOException{

        Group group = groupMapper.selectById(groupId);

        if(group == null){
            throw new BizException("群聊不存在");
        }

        List<String> peoples = stringRedisTemplate.opsForList().range("group:" + groupId, 0, -1);

        if(!peoples.contains(userId)){
            throw new BizException("您不在群聊内");
        }

        Chat chat = new Chat();
        chat.setContent(message);
        chat.setFromId(userId);
        chat.setGroupId(groupId);
        chat.setTime(new Timestamp(System.currentTimeMillis()));

        String result = objectMapper.writeValueAsString(chat);

        for(String people : peoples){
            Session groupPeople = concurrentHashMap.get(people);

            if(groupPeople != null && groupId.equals(groupPeople.getRequestParameterMap().get("group_id").get(0))){
                groupPeople.getBasicRemote().sendText(result);
            }
        }

        stringRedisTemplate.opsForList().leftPush("chat:group:" + groupId, result);
    }

    @Override
    public void searchGroupHistory(Session session, String userId, String groupId, Integer pageSize, Integer pageNum) throws IOException{
        pageNum --;
        Group group = groupMapper.selectById(groupId);

        if(group == null){
            throw new BizException("群聊不存在");
        }

        List<String> peoples = stringRedisTemplate.opsForList().range("group:" + groupId, 0, -1);

        if(!peoples.contains(userId)){
            throw new BizException("您不在群聊内");
        }

        List<String> list1 = stringRedisTemplate.opsForList().range("chat:group:" + groupId, 0, -1);
        Chats chats = new Chats();

        List<Chat> resultList = new ArrayList<>();
        if(list1.size() < pageSize * pageNum + 1){
            
            LambdaQueryWrapper<Chat> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Chat::getGroupId, groupId);
            lambdaQueryWrapper.orderBy(true, false, Chat::getTime);

            lambdaQueryWrapper.last("limit " + (pageSize * pageNum - list1.size()) + ", " + pageSize);
            resultList = chatMapper.selectList(lambdaQueryWrapper);

        }else if(list1.size() >= pageSize * pageNum + pageSize){

            List<String> list = list1.subList(pageSize * pageNum, pageSize * pageNum + pageSize);
            for(String str : list){
                Chat chat = objectMapper.readValue(str, Chat.class);
                resultList.add(chat);
            }
        }else{

            for(int i = pageSize * pageNum; i < list1.size(); i++){
                Chat chat = objectMapper.readValue(list1.get(i), Chat.class);
                resultList.add(chat);
            }

            int already = resultList.size();
            LambdaQueryWrapper<Chat> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Chat::getGroupId, groupId);
            lambdaQueryWrapper.orderBy(true, false, Chat::getTime);

            lambdaQueryWrapper.last("limit " + 0 + ", " + (pageSize - already));
            List<Chat> list = chatMapper.selectList(lambdaQueryWrapper);
            resultList.addAll(list);
        }

        resultList.sort((o1, o2) -> o1.getTime().compareTo(o2.getTime()) * -1);
        chats.setChats(resultList);
        chats.setTotal((long) resultList.size());

        String result = objectMapper.writeValueAsString(chats);
        session.getBasicRemote().sendText(result);

        removeSession(session);
    }
}
