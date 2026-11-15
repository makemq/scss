package com.example.scss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.scss.entity.Enrollment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EnrollmentMapper extends BaseMapper<Enrollment> {
    @Select("SELECT COUNT(*) FROM enrollment WHERE course_id = #{courseId}")
    Long countByCourseId(Long courseId);
}