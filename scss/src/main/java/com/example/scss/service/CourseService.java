
package com.example.scss.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.scss.entity.Course;

import java.util.List;

public interface CourseService {
    // 查询所有课程
    List<Course> listCourses();
    // 根据 ID 查询课程
    Course getCourseById(Long id);
    // 新增课程
    boolean addCourse(Course course);
    // 修改课程
    boolean updateCourse(Course course);
    // 删除课程
    boolean deleteCourse(Long id);
    // 分页查询课程
    IPage<Course> pageCourses(
            String keyword,
            Long pageNum,
            Long pageSize
    );
}
