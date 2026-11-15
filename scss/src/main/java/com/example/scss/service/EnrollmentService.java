package com.example.scss.service;

import com.example.scss.entity.Enrollment;
import com.example.scss.dto.AdminEnrollmentResponse;
import java.util.List;

public interface EnrollmentService {

    // 当前登录学生选课
    boolean selectCourse(Long studentId, Long courseId);

    // 当前登录学生退课
    boolean dropCourse(Long studentId, Long courseId);

    // 查询学生自己的选课记录
    List<Enrollment> listByStudentId(Long studentId);
    // 管理员查询全部选课记录
    List<AdminEnrollmentResponse> listAllEnrollments();
}
