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
import com.example.scss.mapper.StudentMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import com.example.scss.entity.SelectionConfig;
import com.example.scss.service.SelectionConfigService;
@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentMapper enrollmentMapper;
    private final CourseMapper courseMapper;
    private final StudentMapper studentMapper;
    private final SelectionConfigService selectionConfigService;
    public EnrollmentServiceImpl(EnrollmentMapper enrollmentMapper, CourseMapper courseMapper, StudentMapper studentMapper, SelectionConfigService selectionConfigService) {
        this.enrollmentMapper = enrollmentMapper;
        this.courseMapper = courseMapper;
        this.studentMapper = studentMapper;
        this.selectionConfigService = selectionConfigService;
    }

    @Override
    @Transactional
    public boolean selectCourse(Long studentId, Long courseId) {
        //检查是否处于选课范围
        SelectionConfig config = selectionConfigService.getActiveConfig();
        if (config == null) {
            return false;
        }
        //获取时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(config.getStartTime())
                || now.isAfter(config.getEndTime())) {
            return false;
        }
        //锁定课程，避免并发选课超出容量
        Course course = courseMapper.selectByIdForUpdate(courseId);
        //检查课程是否存在、是否正常开放
        if (course == null || course.getStatus() == null || course.getStatus() != 1) {
            return false;
        }
        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enrollment::getStudentId, studentId)
                .eq(Enrollment::getCourseId, courseId);
        Enrollment enrollment = enrollmentMapper.selectOne(wrapper);
        //不能重复选课
        if (enrollment != null && enrollment.getStatus() == 1) {
            return false;
        }
        // 3. 检查是否与学生已选课程时间冲突
        if (hasTimeConflict(studentId, course)) {
            throw new IllegalArgumentException("与已选课程上课时间冲突，不能重复选课");
        }
        // 检查课程是否已满
        if (course.getSelectedCount() >= course.getCapacity()) {
            return false;
        }

        //如果之前退过这门课，恢复原来的选课记录
        if (enrollment != null) {
            enrollment.setStatus(1);
            enrollment.setEnrollTime(LocalDateTime.now());
            enrollmentMapper.updateById(enrollment);
        } else {
            // 7. 如果没有历史记录，新增选课记录
            enrollment = new Enrollment();
            enrollment.setStudentId(studentId);
            enrollment.setCourseId(courseId);
            enrollment.setEnrollTime(LocalDateTime.now());
            enrollment.setStatus(1);
            enrollmentMapper.insert(enrollment);
        }//更新课程已选人数
        course.setSelectedCount(course.getSelectedCount() +1);
        courseMapper.updateById(course);
        return true;
    }
    @Override
    @Transactional
    public boolean dropCourse(Long studentId, Long courseId) {
        //锁定课程，避免并发选课和退课导致人数不一致
        Course course = courseMapper.selectByIdForUpdate(courseId);
        //检查课程是否存在
        if (course == null) {
            return false;
        }
        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enrollment::getStudentId, studentId).eq(Enrollment::getCourseId, courseId).eq(Enrollment::getStatus, 1);
        Enrollment enrollment = enrollmentMapper.selectOne(wrapper);
        //如果没有有效选课记录，说明无法退课
        if (enrollment == null) {
            return false;
        }
        //修改选课记录状态为已退课
        enrollment.setStatus(0);
        enrollmentMapper.updateById(enrollment);
        //更新课程已选人数
        course.setSelectedCount(
                Math.max(0, course.getSelectedCount() - 1)
        );
        courseMapper.updateById(course);
        return true;
    }
    //更新课程成绩
    @Override
    public boolean updateScore(Long enrollmentId, BigDecimal score) {
        //检查成绩范围
        if (score == null
                || score.compareTo(BigDecimal.ZERO) < 0//比较
                || score.compareTo(new BigDecimal("100")) > 0) {
            return false;
        }
        //查询选课记录
        Enrollment enrollment = enrollmentMapper.selectById(enrollmentId);
        //检查记录是否存在，或有效
        if (enrollment == null || enrollment.getStatus() == null
                || enrollment.getStatus() != 1) {
            return false;
        }
        enrollment.setScore(score);
        return enrollmentMapper.updateById(enrollment) > 0;
    }
    @Override
    public List<Enrollment> listByStudentId(Long studentId) {
        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enrollment::getStudentId, studentId).eq(Enrollment::getStatus, 1).orderByDesc(Enrollment::getEnrollTime);
        return enrollmentMapper.selectList(wrapper);
    }
    //查询个人成绩完成
    @Override
    public List<Enrollment> listStudentGrade(Long studentId){
        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enrollment::getStudentId, studentId).eq(Enrollment::getStatus, 1).orderByDesc(Enrollment::getEnrollTime);
        return enrollmentMapper.selectList(wrapper);
    }
    @Override
    public List<AdminEnrollmentResponse> listAllEnrollments() {
        //联表查询
        return enrollmentMapper.selectAllEnrollmentDetails();
    }
    //个人课表查询
    @Override
    public List<Course> getStudentSchedule(Long studentId) {
        return enrollmentMapper.selectStudentSchedule(studentId);
    }
    // 检查学生已选课程是否与新课程时间冲突
    private boolean hasTimeConflict(Long studentId, Course newCourse) {
    LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Enrollment::getStudentId, studentId).eq(Enrollment::getStatus,1);
    List<Enrollment> enrollments = enrollmentMapper.selectList(wrapper);
    for(Enrollment enrollment : enrollments){
        // 查询已选记录对应的课程
        Course oldCourse = courseMapper.selectById(enrollment.getCourseId());
        // 如果课程不存在，跳过
        if (oldCourse == null) {
            continue;
        }
        // 判断上课日期是否相同，且上课节次是否重叠
        if (oldCourse.getClassDay().equals(newCourse.getClassDay())
                && oldCourse.getStartSection() <= newCourse.getEndSection()
                && newCourse.getStartSection() <= oldCourse.getEndSection()) {
            return true;
        }
    }
        // 所有课程检查完毕，没有冲突
        return false;
    }


}
