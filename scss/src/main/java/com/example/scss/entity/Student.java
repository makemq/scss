package com.example.scss.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data // 自动生成 get/set 方法
@TableName("student") // 对应你数据库里的 student 表
public class Student {
    @TableId(type = IdType.AUTO) // 主键自增
    private Long id;
    private String name;
    private String studentNo; // 数据库里是 student_no，这里会自动转驼峰
}
