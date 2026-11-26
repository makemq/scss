package com.example.scss.controller;
import com.example.scss.annotation.RequireRole;
import com.example.scss.config.Result;
import com.example.scss.dto.AdminEnrollmentResponse;
import com.example.scss.entity.Course;
import com.example.scss.entity.Enrollment;
import com.example.scss.service.EnrollmentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import com.example.scss.dto.StudentCreditStatistics;
import com.example.scss.dto.AdminStatisticsResponse;
import com.example.scss.dto.CourseEnrollmentStatistics;
@RestController
@RequestMapping("/enrollment")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }
@PostMapping("/select/{courseId}")
@RequireRole("STUDENT")//选课
public Result<String> selectCourse(@PathVariable Long courseId,HttpServletRequest request){
        //获取学生id
        Long studentId = (Long) request.getAttribute("studentId");
        if (studentId == null) {
            return Result.error(400, "当前账号尚未绑定学生档案");
        }
        if (courseId == null || courseId <= 0) {
            return Result.error(400, "课程ID不合法");
        }
        boolean success = enrollmentService.selectCourse(studentId, courseId);
        if (!success) {
            return Result.error(400, "选课失败：课程不存在、已选过或课程已满");
        }
        return Result.success("选课成功");
    }
@DeleteMapping("/drop/{courseId}")
@RequireRole("STUDENT")//退课
public Result<String> dropCourse(@PathVariable Long courseId,
                                 HttpServletRequest request){
    // 获取当前登录学生ID
    Long studentId = (Long) request.getAttribute("studentId");
    if (studentId == null) {
        return Result.error(400, "当前账号尚未绑定学生档案");
    }
    if (courseId == null || courseId <= 0) {
        return Result.error(400, "课程ID不合法");
    }
    boolean success = enrollmentService.dropCourse(studentId, courseId);
    if (!success) {
        return Result.error(400, "退课失败：未找到有效选课记录");
    }
    return Result.success("退课成功");
}
@GetMapping("/my")
@RequireRole("STUDENT")//自查
public Result<List<Enrollment>> myEnrollments(HttpServletRequest request){
        //获取当前学生id
    Long studentId = (Long) request.getAttribute("studentId");
    if (studentId == null) {
        return Result.error(400, "当前账号尚未绑定学生档案");
    }
    return Result.success(
            enrollmentService.listByStudentId(studentId)
    );
}
@GetMapping("/my/schedule")
@RequireRole("STUDENT")
public Result<List<Course>> getMySchedule(HttpServletRequest request){
        Long studentId = (long) request.getAttribute("studentId");
    if (studentId == null) {
        return Result.error(400, "当前账号尚未绑定学生档案");
    }
        List<Course> schedule = enrollmentService.getStudentSchedule(studentId);
        return Result.success(schedule);
}
@GetMapping("/admin/list")
@RequireRole("ADMIN")//管理员查课
public Result<List<AdminEnrollmentResponse>> listAllEnrollments(){
        return Result.success(enrollmentService.listAllEnrollments());
}
//录入成绩
@PutMapping("/admin/score/{enrollmentId}")
@RequireRole("ADMIN")
public Result<String> updateScore(@PathVariable Long enrollmentId, BigDecimal score){
        if(enrollmentId == null || enrollmentId <= 0){
            return Result.error(400,"你的选课记录ID不合法！");
        }
        boolean success = enrollmentService.updateScore(enrollmentId,score);
        if(!success){
            return Result.error(400,"成绩修改失败，请检查选课记录或成绩范围");
        }
        return Result.success("success");
}
//学生查看自己成绩
@GetMapping("/my/grade")
@RequireRole("STUDENT")
public Result<List<Enrollment>> myGrades(HttpServletRequest request){
       Long studentId = (Long) request.getAttribute("studentId");
       if(studentId == null){
           return Result.error(400,"当前账号尚未绑定学生档案");
       }
       return Result.success(enrollmentService.listStudentGrade(studentId));
    }
@GetMapping("/my/credits")
@RequireRole("STUDENT")
public Result<StudentCreditStatistics> myCredits(HttpServletRequest request){
        Long studentId = (Long) request.getAttribute("studentId");
        if(studentId == null){
            return  Result.error(400,"当前账号尚未绑定学生档案");
        }
        return Result.success(enrollmentService.getStudentCreditStatistics(studentId));
}
//管理员统计数据
@GetMapping("/admin/statistics")
@RequireRole("ADMIN")
public Result<AdminStatisticsResponse> getAdminStatistics(){
        return Result.success(enrollmentService.getAdminStatistics());
}
//管理员查询各课程选课人数统计
public Result<List<CourseEnrollmentStatistics>> getCourseEnrollmentStatistics(){
        return Result.success(enrollmentService.getCourseEnrollmentStatistics());
}
}

