
package com.example.scss.controller;

import com.example.scss.annotation.RequireRole;
import com.example.scss.config.Result;
import com.example.scss.entity.Enrollment;
import com.example.scss.service.EnrollmentService;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import com.example.scss.dto.AdminEnrollmentResponse;
@RestController
@RequestMapping("/enrollment")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // 当前学生选课
    @PostMapping("/select/{courseId}")
    @RequireRole("STUDENT")
    public Result<String> selectCourse(
            @PathVariable Long courseId,
            HttpServletRequest request) {

        Long studentId = (Long) request.getAttribute("studentId");

        if (studentId == null) {
            return Result.error(400, "当前账号尚未绑定学生档案");
        }

        if (courseId == null || courseId <= 0) {
            return Result.error(400, "课程ID不合法");
        }

        boolean success =
                enrollmentService.selectCourse(studentId, courseId);

        if (!success) {
            return Result.error(400, "选课失败：课程不存在、已选过或课程已满");
        }

        return Result.success("选课成功");
    }

    // 当前学生退课
    @DeleteMapping("/drop/{courseId}")
    @RequireRole("STUDENT")
    public Result<String> dropCourse(
            @PathVariable Long courseId,
            HttpServletRequest request) {

        Long studentId = (Long) request.getAttribute("studentId");

        if (studentId == null) {
            return Result.error(400, "当前账号尚未绑定学生档案");
        }

        if (courseId == null || courseId <= 0) {
            return Result.error(400, "课程ID不合法");
        }

        boolean success =
                enrollmentService.dropCourse(studentId, courseId);

        if (!success) {
            return Result.error(400, "退课失败：未找到有效选课记录");
        }

        return Result.success("退课成功");
    }

    // 查询当前学生自己的选课记录
    @GetMapping("/my")
    @RequireRole("STUDENT")
    public Result<List<Enrollment>> myEnrollments(
            HttpServletRequest request) {

        Long studentId = (Long) request.getAttribute("studentId");

        if (studentId == null) {
            return Result.error(400, "当前账号尚未绑定学生档案");
        }

        return Result.success(
                enrollmentService.listByStudentId(studentId)
        );
    }
    // 管理员查询全部选课记录
    @GetMapping("/admin/list")
    @RequireRole("ADMIN")
    public Result<List<AdminEnrollmentResponse>> listAllEnrollments() {
        return Result.success(enrollmentService.listAllEnrollments());
    }
}
