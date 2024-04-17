package com.ygh.service;

import com.ygh.domain.Group;

/**
 * 群聊服务接口
 * @author ygh
 */
public interface GroupService {
    
    /**
     * 创建群聊
     * @param userId
     * @return
     */
    public Group createGroup(Long userId);

    /**
     * 加入群聊
     * @param userId
     * @param groupId
     */
    public void joinGroup(Long userId,Long groupId);
}
