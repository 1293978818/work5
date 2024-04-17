package com.ygh.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * 用户实体类
 * @author ygh
 */
@Data
public class User {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String username;

    @JsonIgnore
    private String password;
}
