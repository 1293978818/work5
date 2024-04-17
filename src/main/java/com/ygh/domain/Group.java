package com.ygh.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群组实体类
 * @author ygh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("`group`")
public class Group {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long groupId;

    private Integer memberCount;
}
