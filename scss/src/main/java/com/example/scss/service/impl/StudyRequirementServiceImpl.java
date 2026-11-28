package com.example.scss.service.impl;

import com.example.scss.ai.AiCourseService;
import com.example.scss.dto.StudyProgressResponse;
import com.example.scss.dto.StudyRequirementResponse;
import com.example.scss.service.StudyProgressService;
import com.example.scss.service.StudyRequirementService;
import org.springframework.stereotype.Service;

@Service
public class StudyRequirementServiceImpl implements StudyRequirementService {
    private final StudyProgressService studyProgressService;
    public StudyRequirementServiceImpl(StudyProgressService studyProgressService) {
        this.studyProgressService = studyProgressService;
    }
    @Override
    public StudyRequirementResponse checkRequirements(Long studentId) {
        // 查询学生真实修读进度
        StudyProgressResponse progress = studyProgressService.getStudyProgress(studentId);
        StudyRequirementResponse response = new StudyRequirementResponse();
        // 判断四类课程学分是否达标
        boolean social = progress.getSocialCredits() >= 1;
        boolean natural = progress.getNaturalCredits() >= 1;
        boolean art = progress.getArtCredits() >= 1;
        boolean ethnic = progress.getEthnicCredits() >= 1;
        // 判断线下课程数量是否达标
        boolean offline = progress.getCompletedOfflineCourses() >= 2;
        response.setSocialSatisfied(social);
        response.setNaturalSatisfied(natural);
        response.setArtSatisfied(art);
        response.setEthnicSatisfied(ethnic);
        response.setOfflineSatisfied(offline);
        // 统计未满足的课程类别
        int unmetCount = 0;
        if (!social) unmetCount++;
        if (!natural) unmetCount++;
        if (!art) unmetCount++;
        if (!ethnic) unmetCount++;
        response.setUnmetCategoryCount(unmetCount);
        return response;
    }
}
