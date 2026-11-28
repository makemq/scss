package com.example.scss.dto;
import com.example.scss.entity.Course;
import lombok.Data;
import java.util.List;
@Data
public class CourseRecommendResponse {
    private String reason;
    //推荐课程
    private List<Course> courses;
}
