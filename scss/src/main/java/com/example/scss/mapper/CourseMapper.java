package com.example.scss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.scss.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    @Select("SELECT * " +
            "FROM course " +
            "WHERE id = #{id} " +
            "FOR UPDATE")
    Course selectByIdForUpdate(Long id);
}
