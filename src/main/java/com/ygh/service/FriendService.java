package com.ygh.service;

import java.util.List;

import com.ygh.domain.User;

/**
 * 联系人服务接口
 * @author ygh
 */
public interface FriendService {
   
    /**
     * 添加联系人
     * @param userId
     * @param friendId
     */
    public void addFriend(Long userId, Long friendId);

    /**
     * 删除联系人
     * @param userId
     * @param friendId
     */
    public void deleteFriend(Long userId, Long friendId);

    /**
     * 查询朋友信息
     * @param userId
     * @param pageSize
     * @param pageNum
     * @return
     */
    public List<User> selectFriend(Long userId,Integer pageSize,Integer pageNum);

}
