package com.example.scss.service;

import com.example.scss.dto.StudyProgressResponse;

public interface StudyProgressService {
    StudyProgressResponse getStudyProgress(Long studentId);
}
