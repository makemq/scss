package com.example.scss.mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.scss.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    //根据课程ID查询并加行锁
    @Select("SELECT * FROM course WHERE id = #{id} FOR UPDATE")
    Course selectByIdForUpdate(Long id);
    //课程人数增加
    @Update("UPDATE course SET selected_count = selected_count + 1 WHERE id = #{id}")
    int increaseSelectedCount(Long id);
    //课程人数减少
    @Update("UPDATE course SET selected_count = GREATEST(selected_count - 1, 0) WHERE id = #{id}")
    int decreaseSelectedCount(Long id);
    //查询正常开放且未满额的选修课程
    @Select("""
    SELECT *
    FROM course
    WHERE course_category = 'ELECTIVE'
      AND status = 1
      AND selected_count < capacity
      AND (#{courseType} IS NULL OR #{courseType} = '' OR course_type = #{courseType})
      AND (#{teachingMode} IS NULL OR #{teachingMode} = '' OR teaching_mode = #{teachingMode})
      AND (#{teacher} IS NULL OR #{teacher} = ''
           OR teacher LIKE CONCAT('%', #{teacher}, '%'))
    """)
    List<Course> selectAvailableElectiveCourses(
            @Param("courseType") String courseType,
            @Param("teachingMode") String teachingMode,
            @Param("teacher") String teacher
    );
}