package com.example.scss.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@TableName("course")
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
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
    //课程类别
    private String courseCategory;
    //课程性质
    private String courseType;
    //授课方式
    private String teachingMode;
}
