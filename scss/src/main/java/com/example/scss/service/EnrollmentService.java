package com.example.scss.service;
import com.example.scss.entity.Course;
import com.example.scss.entity.Enrollment;
import com.example.scss.dto.AdminEnrollmentResponse;
import java.math.BigDecimal;
import java.util.List;
import com.example.scss.dto.StudentCreditStatistics;
import com.example.scss.dto.AdminStatisticsResponse;
import com.example.scss.dto.CourseEnrollmentStatistics;
public interface EnrollmentService {
    // 当前登录学生选课
    boolean selectCourse(Long studentId, Long courseId);
    // 当前登录学生退课
    boolean dropCourse(Long studentId, Long courseId);
    // 查询学生自己的选课记录
    List<Enrollment> listByStudentId(Long studentId);
    // 管理员查询全部选课记录
    List<AdminEnrollmentResponse> listAllEnrollments();
    //查询学生个人课表
    List<Course> getStudentSchedule(Long studentId);
    //管理员录入或修改学生成绩
    boolean updateScore(Long enrollmentId, BigDecimal score);
    //学生查询选课成绩
    List<Enrollment> listStudentGrade(Long studentId);
    //查询学生学分统计
    StudentCreditStatistics getStudentCreditStatistics(Long studentId);
    // 管理员查询系统基础统计数据
    AdminStatisticsResponse getAdminStatistics();
    // 管理员查询各课程选课人数统计
    List<CourseEnrollmentStatistics> getCourseEnrollmentStatistics();
}
