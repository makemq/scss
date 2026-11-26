package com.example.scss.dto;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class StudentCreditStatistics {
    // 已选课程数
    private Integer selectedCourseCount;
    // 已修课程数（成绩 >= 60）
    private Integer completedCourseCount;
    // 已获得学分
    private BigDecimal earnedCredits;
    // 未通过课程数（成绩 < 60）
    private Integer failedCourseCount;
    // 待出成绩课程数
    private Integer pendingCourseCount;
}
