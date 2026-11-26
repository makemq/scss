package com.example.scss.dto;

import lombok.Data;

@Data
public class AdminStatisticsResponse {
    // 学生总数
    private Long studentCount;
    // 课程总数
    private Long courseCount;
    // 有效选课总数
    private Long enrollmentCount;
}
