package com.example.scss.service.impl;

import com.example.scss.dto.StudyProgressResponse;
import com.example.scss.mapper.EnrollmentMapper;
import com.example.scss.service.StudyProgressService;
import org.springframework.stereotype.Service;

@Service
public class StudyProgressServiceImpl implements StudyProgressService {
    private final EnrollmentMapper enrollmentMapper;
    public StudyProgressServiceImpl(EnrollmentMapper enrollmentMapper) {
        this.enrollmentMapper = enrollmentMapper;
    }
    @Override
    public StudyProgressResponse getStudyProgress(Long studentId) {
        return enrollmentMapper.selectStudyProgress(studentId);
    }
}
