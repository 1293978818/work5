package com.ygh.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ygh.domain.User;

/**
 * 用户Mapper接口
 * @author ygh
 */
@Mapper
public interface UserMapper extends BaseMapper<User>{
    
}
