
package com.example.scss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scss.entity.Course;
import com.example.scss.mapper.CourseMapper;
import com.example.scss.service.CourseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;

    public CourseServiceImpl(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    // 查询所有课程
    @Override
    public List<Course> listCourses() {
        return courseMapper.selectList(null);
    }

    // 根据 ID 查询课程
    @Override
    public Course getCourseById(Long id) {
        return courseMapper.selectById(id);
    }

    // 新增课程
    @Override
    public boolean addCourse(Course course) {
        // 新增课程时，初始化已选人数和课程状态
        if (course.getSelectedCount() == null) {
            course.setSelectedCount(0);
        }

        if (course.getStatus() == null) {
            course.setStatus(1);
        }

        return courseMapper.insert(course) > 0;
    }

    // 修改课程
    @Override
    public boolean updateCourse(Course course) {
        // 检查课程是否存在
        Course oldCourse = courseMapper.selectById(course.getId());

        if (oldCourse == null) {
            return false;
        }

        return courseMapper.updateById(course) > 0;
    }

    // 删除课程
    @Override
    public boolean deleteCourse(Long id) {
        // 检查课程是否存在
        Course course = courseMapper.selectById(id);

        if (course == null) {
            return false;
        }

        return courseMapper.deleteById(id) > 0;
    }

    // 分页查询 + 关键词模糊搜索
    @Override
    public IPage<Course> pageCourses(
            String keyword,
            Long pageNum,
            Long pageSize) {

        // 创建分页对象
        Page<Course> page = new Page<>(pageNum, pageSize);

        // 创建查询条件
        LambdaQueryWrapper<Course> wrapper =
                new LambdaQueryWrapper<>();

        // 关键词搜索课程名称或教师
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = keyword.trim();

            wrapper.and(w -> w
                    .like(Course::getCourseName, searchKeyword)
                    .or()
                    .like(Course::getTeacher, searchKeyword)
            );
        }

        return courseMapper.selectPage(page, wrapper);
    }
}

