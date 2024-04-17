package com.ygh.domain;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天实体类
 * @author ygh
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    
    private String fromId;

    private String toUserId;

    private String groupId;

    private String content;

    private Timestamp time;

    @JsonIgnore
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
}
