
package com.example.scss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scss.entity.Course;
import com.example.scss.mapper.CourseMapper;
import com.example.scss.service.CourseService;
import org.springframework.stereotype.Service;
import com.example.scss.mapper.EnrollmentMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseMapper courseMapper;
    private final EnrollmentMapper enrollmentMapper;
    public CourseServiceImpl(CourseMapper courseMapper, EnrollmentMapper enrollmentMapper) {
        this.courseMapper = courseMapper;
        this.enrollmentMapper = enrollmentMapper;
    }
    @Override
    public List<Course> listCourses() {
        return courseMapper.selectList(null);
    }
    @Override
    public Course getCourseById(Long id) {
        return courseMapper.selectById(id);
    }
    @Override
    public boolean addCourse(Course course) {
        //初始化
        if(course.getSelectedCount()==null){
            course.setSelectedCount(0);
        }
        //课程状态1正常
        if(course.getStatus()== 1){
            course.setStatus(1);
        }
        return courseMapper.insert(course) > 0;
    }
    @Override
    public boolean updateCourse(Course course) {
        //检查课程是否存在
        Course oldCourse = courseMapper.selectByIdForUpdate(course.getId());
        if (oldCourse == null) {
            return false;
        }
        return courseMapper.updateById(course) > 0;
    }
    @Override
    @Transactional
    public boolean deleteCourse(Long id) {
        return false;
    }
    @Override
    public IPage<Course> pageCourses(String keyword, Long pageNum, Long pageSize) {
        return null;
    }
}

