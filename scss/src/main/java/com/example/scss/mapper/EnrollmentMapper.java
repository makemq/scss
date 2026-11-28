package com.example.scss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.scss.dto.AdminEnrollmentResponse;
import com.example.scss.dto.CourseEnrollmentStatistics;
import com.example.scss.dto.StudyProgressResponse;
import com.example.scss.entity.Course;
import com.example.scss.entity.Enrollment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EnrollmentMapper extends BaseMapper<Enrollment> {
    @Select("SELECT COUNT(*) FROM enrollment WHERE course_id = #{courseId} AND status = 1")
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
    @Select("""
    SELECT
        c.id AS id,
        c.course_name AS courseName,
        c.teacher AS teacher,
        c.credit AS credit,
        c.class_day AS classDay,
        c.start_section AS startSection,
        c.end_section AS endSection
    FROM enrollment e
    INNER JOIN course c ON e.course_id = c.id
    WHERE e.student_id = #{studentId}
      AND e.status = 1
      AND c.status = 1
    ORDER BY c.class_day, c.start_section
    """)
    //统计所有课程的有效选课人数
    List<Course> selectStudentSchedule(Long studentId);
    // 查询学生已经选过的课程ID
    @Select("""
    SELECT course_id
    FROM enrollment
    WHERE student_id = #{studentId}
      AND status = 1
    """)
    List<Long> selectStudentEnrolledCourseIds(Long studentId);
    @Select("""
    SELECT
        course_id AS courseId,
        COUNT(*) AS enrollmentCount
    FROM enrollment
    WHERE status = 1
    GROUP BY course_id
    """)
    List<CourseEnrollmentStatistics> countAllCourseEnrollments();
    @Select("""
    SELECT
        COALESCE(SUM(CASE
            WHEN c.course_type = '社科'
             AND e.score >= 60
            THEN c.credit ELSE 0 END), 0) AS socialCredits,

        COALESCE(SUM(CASE
            WHEN c.course_type = '自然'
             AND e.score >= 60
            THEN c.credit ELSE 0 END), 0) AS naturalCredits,

        COALESCE(SUM(CASE
            WHEN c.course_type = '艺体'
             AND e.score >= 60
            THEN c.credit ELSE 0 END), 0) AS artCredits,

        COALESCE(SUM(CASE
            WHEN c.course_type = '民族'
             AND e.score >= 60
            THEN c.credit ELSE 0 END), 0) AS ethnicCredits,

        COALESCE(SUM(CASE
            WHEN c.teaching_mode = '线下'
             AND e.score >= 60
            THEN 1 ELSE 0 END), 0) AS completedOfflineCourses,

        COALESCE(SUM(CASE
            WHEN e.score IS NULL
            THEN 1 ELSE 0 END), 0) AS pendingCourses,

        COALESCE(SUM(CASE
            WHEN e.score < 60
            THEN 1 ELSE 0 END), 0) AS failedCourses

    FROM enrollment e
    JOIN course c ON e.course_id = c.id
    WHERE e.student_id = #{studentId}
      AND e.status = 1
      AND c.course_category = 'ELECTIVE'
    """)
    StudyProgressResponse selectStudyProgress(Long studentId);
}
