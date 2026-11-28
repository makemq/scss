package com.example.scss.dto;

import lombok.Data;
@Data
public class CourseRecommendRequest {
    // 兴趣领域
    private String interest;
    // 学习目标
    private String learningGoal;
    // 指定教师，不指定则为空
    private String teacher;
    // 希望的课程性质
    private String courseType;
    // 希望的授课方式
    private String teachingMode;
}
