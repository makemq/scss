package com.example.scss.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.scss.config.Result;
import com.example.scss.entity.Course;
import com.example.scss.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.example.scss.annotation.RequireRole;
@RestController
@RequestMapping("/course")
public class CourseController {
    private final CourseService courseService;
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }
    //查询所有课程
    @GetMapping("/list")
    public Result<List<Course>> listCourses(){
        return Result.success(courseService.listCourses());
    }
    // 根据ID查询课程
    @GetMapping("/{id}")
    public Result<Course> getCourseById(@PathVariable Long id){
        Course course = courseService.getCourseById(id);
        if(course == null){
            return Result.error(404, "课程不存在");
        }
        return Result.success(course);
    }
    @RequireRole("ADMIN")
    @PostMapping("/add")
    public Result<String> addCourse(@RequestBody Course course){
        if (course == null) {
            return Result.error(400, "课程信息不能为空");
        }
        if (course.getCourseName() == null
                || course.getCourseName().isBlank()) {
            return Result.error(400, "课程名称不能为空");
        }
        if (course.getTeacher() == null
                || course.getTeacher().isBlank()) {
            return Result.error(400, "教师名称不能为空");
        }
        if (course.getCapacity() == null || course.getCapacity() <= 0) {
            return Result.error(400, "课程容量必须大于0");
        }
        boolean success = courseService.addCourse(course);
        if (!success) {
            return Result.error(400, "新增课程失败");
        }
        return Result.success("新增课程成功");
    }
    @RequireRole("ADMIN")
    @PutMapping("/update")
    public Result<String> updateCourse(@RequestBody Course course){
        if (course == null || course.getId() == null) {
            return Result.error(400, "课程ID不合法");
        }
        if (course.getCourseName() == null
                || course.getCourseName().isBlank()) {
            return Result.error(400, "课程名称不能为空");
        }
        if (course.getTeacher() == null
                || course.getTeacher().isBlank()) {
            return Result.error(400, "教师名称不能为空");
        }
        boolean success = courseService.updateCourse(course);
        if (!success) {
            return Result.error(400, "修改课程失败");
        }
        return Result.success("修改课程成功");
    }
    @RequireRole("ADMIN")
    @DeleteMapping("/{id}")
    public  Result<String> deleteCourseById(@PathVariable Long id){
        if(id == null|| id<= 0){
            return Result.error(400, "课程ID不能为空为零");
        }
        boolean success = courseService.deleteCourse(id);
        if(!success){
            return Result.error(400, "删除课程失败，课程不存在或存在有效选课记录");
        }
        return Result.success("删除课程成功");
    }
    @GetMapping("/page")
    public Result<IPage<Course>> pageCourses(@RequestParam (required = false) String keyword,
                                            @RequestParam (defaultValue = "1") Long pageNum,
                                            @RequestParam (defaultValue = "10") Long pageSize
                         ){
        if (pageNum < 1 || pageSize < 1 || pageSize > 100) {
            return Result.error(400, "分页参数不合法");
        }
        IPage<Course> page = courseService.pageCourses(keyword, pageNum, pageSize);
        return Result.success(page);
    }

}

