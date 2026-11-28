package com.example.scss.ai;

import com.example.scss.annotation.RequireRole;
import com.example.scss.dto.CourseRecommendRequest;
import com.example.scss.dto.CourseRecommendResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.scss.config.Result;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/ai/course")
public class AiCourseController {
    private final AiCourseService aiCourseService;
    public AiCourseController(AiCourseService aiCourseService) {
        this.aiCourseService = aiCourseService;
    }
    @RequireRole("STUDENT")
    @PostMapping("/recommend")
    public Result<CourseRecommendResponse> recommend(
            @RequestBody CourseRecommendRequest request,
            @RequestAttribute("studentId") Long studentId
    ) {
        CourseRecommendResponse response = aiCourseService.recommendCourses(studentId, request);
        return Result.success(response);
    }
}
