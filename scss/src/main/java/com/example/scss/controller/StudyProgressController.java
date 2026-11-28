package com.example.scss.controller;

import com.example.scss.config.Result;
import com.example.scss.dto.StudyProgressResponse;
import com.example.scss.service.StudyProgressService;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student/progress")
public class StudyProgressController {
    private final StudyProgressService studyProgressService;
    public StudyProgressController(StudyProgressService studyProgressService) {
        this.studyProgressService = studyProgressService;
    }
    public Result<StudyProgressResponse> getProgress( @RequestAttribute("studentId") Long studentId){
        StudyProgressResponse response = studyProgressService.getStudyProgress(studentId);
        return Result.success(response);
    }
}
