package com.example.scss.dto;
import lombok.Data;
@Data
public class CourseEnrollmentStatistics {
    // 课程 ID
    private Long courseId;
    // 课程名称
    private String courseName;
    // 课程容量
    private Integer capacity;
    // 当前有效选课人数
    private Long enrollmentCount;
}
