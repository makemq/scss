package com.example.scss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.scss.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {


    // 只要继承了 BaseMapper，就已经自带了增删改查所有方法，不用写 SQL！
}
