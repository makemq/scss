package com.example.scss.ai;

import com.example.scss.dto.CourseRecommendRequest;
import com.example.scss.dto.CourseRecommendResponse;
import com.example.scss.dto.StudyRequirementResponse;
import com.example.scss.entity.Course;

public interface AiCourseService {
    //根据学生兴趣与实际进行AI推荐
    CourseRecommendResponse recommendCourses(Long studentId, CourseRecommendRequest request);

}
