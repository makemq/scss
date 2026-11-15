package com.example.scss.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminEnrollmentResponse {

    private Long id;

    private Long studentId;

    private String studentName;

    private String studentNo;

    private Long courseId;

    private String courseName;

    private String teacher;

    private LocalDateTime enrollTime;

    private Integer status;
}

