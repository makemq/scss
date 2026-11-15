package com.example.scss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.scss.dto.AdminEnrollmentResponse;
import com.example.scss.entity.Enrollment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EnrollmentMapper extends BaseMapper<Enrollment> {
    @Select("SELECT COUNT(*) FROM enrollment WHERE course_id = #{courseId}")
    Long countByCourseId(Long courseId);
    @Select("""
    SELECT
        e.id AS id,
        e.student_id AS studentId,
        s.name AS studentName,
        s.student_no AS studentNo,
        e.course_id AS courseId,
        c.course_name AS courseName,
        c.teacher AS teacher,
        e.enroll_time AS enrollTime,
        e.status AS status
    FROM enrollment e
    LEFT JOIN student s ON e.student_id = s.id
    LEFT JOIN course c ON e.course_id = c.id
    ORDER BY e.enroll_time DESC
    """)
    List<AdminEnrollmentResponse> selectAllEnrollmentDetails();
}
