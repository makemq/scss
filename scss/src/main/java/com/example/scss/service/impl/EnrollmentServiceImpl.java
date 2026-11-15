
package com.example.scss.service.impl;
import com.example.scss.dto.AdminEnrollmentResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.scss.entity.Course;
import com.example.scss.entity.Enrollment;
import com.example.scss.mapper.CourseMapper;
import com.example.scss.mapper.EnrollmentMapper;
import com.example.scss.service.EnrollmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentMapper enrollmentMapper;
    private final CourseMapper courseMapper;

    public EnrollmentServiceImpl(
            EnrollmentMapper enrollmentMapper,
            CourseMapper courseMapper) {
        this.enrollmentMapper = enrollmentMapper;
        this.courseMapper = courseMapper;
    }
    @Override
    public List<AdminEnrollmentResponse> listAllEnrollments() {
        return enrollmentMapper.selectAllEnrollmentDetails();
    }
    // 选课
    @Override
    @Transactional
    public boolean selectCourse(Long studentId, Long courseId) {

        // 检查课程是否存在
        Course course = courseMapper.selectById(courseId);
        if (course == null || course.getStatus() == null
                || course.getStatus() != 1) {
            return false;
        }
        // 检查是否与已选课程时间冲突
        if (hasTimeConflict(studentId, course)) {
            throw new IllegalArgumentException("与已选课程上课时间冲突，不能重复选课");
        }
        // 检查是否已经选过
        LambdaQueryWrapper<Enrollment> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(Enrollment::getStudentId, studentId)
                .eq(Enrollment::getCourseId, courseId);

        Enrollment oldEnrollment =
                enrollmentMapper.selectOne(wrapper);

        if (oldEnrollment != null) {
            // 已经选课，不允许重复选
            if (oldEnrollment.getStatus() != null
                    && oldEnrollment.getStatus() == 1) {
                return false;
            }

            // 曾经退选过，恢复选课记录
            if (course.getSelectedCount() >= course.getCapacity()) {
                return false;
            }

            oldEnrollment.setStatus(1);
            oldEnrollment.setEnrollTime(LocalDateTime.now());

            course.setSelectedCount(course.getSelectedCount() + 1);

            enrollmentMapper.updateById(oldEnrollment);
            courseMapper.updateById(course);
            return true;
        }

        // 检查课程容量
        if (course.getSelectedCount() >= course.getCapacity()) {
            return false;
        }

        // 创建选课记录
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setEnrollTime(LocalDateTime.now());
        enrollment.setStatus(1);

        // 更新课程已选人数
        course.setSelectedCount(course.getSelectedCount() + 1);

        enrollmentMapper.insert(enrollment);
        courseMapper.updateById(course);

        return true;
    }
    // 检查学生是否存在上课时间冲突
    private boolean hasTimeConflict(Long studentId, Course newCourse) {

        LambdaQueryWrapper<Enrollment> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(Enrollment::getStudentId, studentId)
                .eq(Enrollment::getStatus, 1);

        List<Enrollment> enrollments =
                enrollmentMapper.selectList(wrapper);

        for (Enrollment enrollment : enrollments) {

            // 跳过同一门课程
            if (enrollment.getCourseId().equals(newCourse.getId())) {
                continue;
            }

            Course oldCourse =
                    courseMapper.selectById(enrollment.getCourseId());

            if (oldCourse == null) {
                continue;
            }

            // 同一天，并且节次有重叠
            if (oldCourse.getClassDay().equals(newCourse.getClassDay())
                    && oldCourse.getStartSection() <= newCourse.getEndSection()
                    && newCourse.getStartSection() <= oldCourse.getEndSection()) {
                return true;
            }
        }

        return false;
    }
    // 退课
    @Override
    @Transactional
    public boolean dropCourse(Long studentId, Long courseId) {

        LambdaQueryWrapper<Enrollment> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(Enrollment::getStudentId, studentId)
                .eq(Enrollment::getCourseId, courseId)
                .eq(Enrollment::getStatus, 1);

        Enrollment enrollment =
                enrollmentMapper.selectOne(wrapper);

        if (enrollment == null) {
            return false;
        }

        Course course = courseMapper.selectById(courseId);

        if (course == null) {
            return false;
        }

        enrollment.setStatus(0);
        course.setSelectedCount(
                Math.max(0, course.getSelectedCount() - 1)
        );

        enrollmentMapper.updateById(enrollment);
        courseMapper.updateById(course);

        return true;
    }

    // 查询学生选课记录
    @Override
    public List<Enrollment> listByStudentId(Long studentId) {

        LambdaQueryWrapper<Enrollment> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(Enrollment::getStudentId, studentId)
                .orderByDesc(Enrollment::getEnrollTime);

        return enrollmentMapper.selectList(wrapper);
    }
}
