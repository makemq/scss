package com.example.scss.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.scss.config.Result;
import com.example.scss.entity.Course;
import com.example.scss.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // 查询所有课程
    @GetMapping("/list")
    public Result<List<Course>> listCourses() {
        return Result.success(courseService.listCourses());
    }

    // 根据 ID 查询课程
    @GetMapping("/{id}")
    public Result<Course> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);

        if (course == null) {
            return Result.error(404, "课程不存在");
        }

        return Result.success(course);
    }

    // 新增课程
    @PostMapping("/add")
    public Result<String> addCourse(@RequestBody Course course) {
        String error = validateCourse(course);
        if (error != null) {
            return Result.error(400, error);
        }

        boolean success = courseService.addCourse(course);

        if (!success) {
            return Result.error(400, "课程新增失败");
        }

        return Result.success("课程新增成功");
    }

    // 修改课程
    @PutMapping("/update")
    public Result<String> updateCourse(@RequestBody Course course) {
        if (course.getId() == null || course.getId() <= 0) {
            return Result.error(400, "课程ID不合法");
        }

        String error = validateCourse(course);
        if (error != null) {
            return Result.error(400, error);
        }

        boolean success = courseService.updateCourse(course);

        if (!success) {
            return Result.error(400, "课程不存在或修改失败");
        }

        return Result.success("课程修改成功");
    }

    // 删除课程
    @DeleteMapping("/{id}")
    public Result<String> deleteCourse(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error(400, "课程ID不合法");
        }

        boolean success = courseService.deleteCourse(id);

        if (!success) {
            return Result.error(404, "课程不存在或删除失败");
        }

        return Result.success("课程删除成功");
    }

    // 分页查询 + 关键词搜索
    @GetMapping("/page")
    public Result<IPage<Course>> pageCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize) {

        if (pageNum < 1 || pageSize < 1 || pageSize > 100) {
            return Result.error(400, "分页参数不合法");
        }

        IPage<Course> page = courseService.pageCourses(
                keyword, pageNum, pageSize
        );

        return Result.success(page);
    }

    // 校验课程信息
    private String validateCourse(Course course) {
        if (course.getCourseName() == null ||
                course.getCourseName().trim().isEmpty()) {
            return "课程名称不能为空";
        }

        if (course.getTeacher() == null ||
                course.getTeacher().trim().isEmpty()) {
            return "教师姓名不能为空";
        }

        if (course.getCredit() == null ||
                course.getCredit().signum() < 0) {
            return "课程学分不能小于0";
        }

        if (course.getCapacity() == null ||
                course.getCapacity() < 1) {
            return "课程容量必须大于0";
        }

        if (course.getClassDay() == null ||
                course.getClassDay() < 1 ||
                course.getClassDay() > 7) {
            return "上课星期必须在1到7之间";
        }

        if (course.getStartSection() == null ||
                course.getEndSection() == null ||
                course.getStartSection() < 1 ||
                course.getEndSection() < course.getStartSection()) {
            return "上课节次不合法";
        }

        return null;
    }
}

