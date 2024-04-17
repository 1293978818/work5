package com.ygh.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天列表实体类
 * @author ygh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chats {
    
    private List<Chat> chats;

    private Long total;
}
