package com.example.scss.service;

import com.example.scss.dto.StudyRequirementResponse;

public interface StudyRequirementService {
    StudyRequirementResponse checkRequirements(Long studentId);
}
