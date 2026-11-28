package com.example.scss.controller;
import com.example.scss.annotation.RequireRole;
import com.example.scss.config.Result;
import com.example.scss.dto.StudyRequirementResponse;
import com.example.scss.service.StudyRequirementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/student/requirements")
public class StudyRequirementController {
    private final StudyRequirementService studyRequirementService;
    public StudyRequirementController(
            StudyRequirementService studyRequirementService) {
        this.studyRequirementService = studyRequirementService;
    }
    // 查询当前学生的修读要求完成情况
    @RequireRole("STUDENT")
    @GetMapping
    public Result<StudyRequirementResponse> checkRequirements(@RequestAttribute("studentId") Long studentId) {
        StudyRequirementResponse response = studyRequirementService.checkRequirements(studentId);
        return Result.success(response);
    }
}
