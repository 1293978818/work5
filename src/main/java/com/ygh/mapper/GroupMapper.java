package com.ygh.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ygh.domain.Group;

/**
 * 群组Mapper接口
 * @author ygh
 */
@Mapper
public interface GroupMapper extends BaseMapper<Group>{
    
}
