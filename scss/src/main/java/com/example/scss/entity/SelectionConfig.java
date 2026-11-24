package com.example.scss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("selection_config")
public class SelectionConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    //学期
    private String semester;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    //表示状态
    private Integer isActive;
}
