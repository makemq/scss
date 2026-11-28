package com.example.scss.dto;

import lombok.Data;

@Data
public class StudyProgressResponse {
    //社科已通过学分
    private Double socialCredits;
    //自然已通过学分
    private Double naturalCredits;
    //艺体已通过学分
    private Double artCredits;
    //民族已通过学分
    private Double ethnicCredits;
    //已完成的线下课程数量
    private Integer completedOfflineCourses;
    //已选但尚未出成绩的课程数量
    private Integer pendingCourses;
    //未通过课程数量
    private Integer failedCourses;
}
