package com.ygh.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ygh.domain.Chat;

/**
 * 聊天记录Mapper接口
 * @author ygh
 */
@Mapper
public interface ChatMapper extends BaseMapper<Chat>{
    
}
