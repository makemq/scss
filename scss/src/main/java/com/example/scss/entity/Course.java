
package com.example.scss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("course")
public class Course {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String courseName;
    private String teacher;
    private BigDecimal credit;
    private Integer capacity;
    private Integer selectedCount;
    private Integer classDay;
    private Integer startSection;
    private Integer endSection;
    private Integer status;
}
