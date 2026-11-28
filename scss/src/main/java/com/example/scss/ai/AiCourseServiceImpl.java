package com.example.scss.ai;

import com.example.scss.dto.CourseRecommendRequest;
import com.example.scss.dto.CourseRecommendResponse;
import com.example.scss.dto.StudyProgressResponse;
import com.example.scss.entity.Course;
import com.example.scss.mapper.CourseMapper;
import com.example.scss.mapper.EnrollmentMapper;
import com.example.scss.service.StudyProgressService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.example.scss.dto.AiRecommendResult;
import java.util.Objects;
import com.example.scss.dto.StudyRequirementResponse;
import com.example.scss.service.StudyRequirementService;
@Service
public class AiCourseServiceImpl implements AiCourseService {
    private final ChatClient chatClient;
    private final CourseMapper courseMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final ObjectMapper objectMapper;
    private final StudyProgressService studyProgressService;
    private final StudyRequirementService studyRequirementService;
    public AiCourseServiceImpl(
            ChatClient.Builder builder,
            CourseMapper courseMapper,
            EnrollmentMapper enrollmentMapper,
            ObjectMapper objectMapper,
            StudyProgressService studyProgressService,
            StudyRequirementService studyRequirementService
    ) {
        this.chatClient = builder.build();
        this.courseMapper = courseMapper;
        this.enrollmentMapper = enrollmentMapper;
        this.objectMapper = objectMapper;
        this.studyProgressService = studyProgressService;
        this.studyRequirementService = studyRequirementService;
    }
    @Override
    public CourseRecommendResponse recommendCourses(Long studentId, CourseRecommendRequest request) {
        //查询开放且未满员的选课
        List<Course> candidates = courseMapper.selectAvailableElectiveCourses(request.getCourseType(), request.getTeachingMode(), request.getTeacher());
        //查询学生选过的课程ID
        //没有推荐返回
        if(candidates.isEmpty()){
            CourseRecommendResponse response = new CourseRecommendResponse();
            response.setReason("目前没有合适的课程，以后再试");
            response.setCourses(new ArrayList<>());
            return response;
        }
        Set<Long> enrolledIds = new HashSet<>(enrollmentMapper.selectStudentEnrolledCourseIds(studentId));
        //查询学生当前课表
        List<Course> schedule = enrollmentMapper.selectStudentSchedule(studentId);
        //排除已经选修课程
        candidates.removeIf(course -> enrolledIds.contains(course.getId())
        );
        //排除时间冲突的课程
        candidates.removeIf(candidate ->
                schedule.stream().anyMatch(selected -> Objects.equals(candidate.getClassDay(), selected.getClassDay()) && candidate.getStartSection() != null && candidate.getEndSection() != null && selected.getStartSection() != null && selected.getEndSection() != null && candidate.getStartSection() <= selected.getEndSection() && candidate.getEndSection() >= selected.getStartSection()
                )
        );
        //进行时间冲突
        if(candidates.isEmpty()){
            CourseRecommendResponse response = new CourseRecommendResponse();
            response.setReason("目前课程与选课冲突");
            response.setCourses(new ArrayList<>());
            return response;
        }
        try {
            //查询进度
            StudyProgressResponse progress = studyProgressService.getStudyProgress(studentId);
            StudyRequirementResponse requirements = studyRequirementService.checkRequirements(studentId);
            //将真实候选课程转换为JSON
            String courseJson = objectMapper.writeValueAsString(candidates);
            // 构建 AI 提示词
            String prompt = """
            你是一个大学智能选课助手。
            请根据学生的兴趣和学习目标，从提供的候选课程中推荐最多3门课程。
            学生需求：
            兴趣领域：%s
            学习目标：%s
            指定教师：%s
            课程性质：%s
            授课方式：%s
                    
            学生当前修读进度：
            社科已获得学分：%s
            自然已获得学分：%s
            艺体已获得学分：%s
            民族已获得学分：%s
            已完成线下课程数量：%s
            待出成绩课程数量：%s
            未通过课程数量：%s
                    
            学校修读要求：
            1. 社科至少获得1学分。
            2. 自然至少获得1学分。
            3. 艺体至少获得1学分。
            4. 民族至少获得1学分。
            5. 至少完成2门线下课程。
            6. 只有成绩达到60分的有效选修课才算完成。
            7. 优先推荐学生尚未满足要求的课程类别。
            学生当前修读要求检查结果：
            社科是否满足：%s
            自然是否满足：%s
            艺体是否满足：%s
            民族是否满足：%s
            线下课程数量是否满足：%s       
            推荐时必须遵守：
            1. 优先推荐尚未满足学分要求的课程类别。
            2. 如果线下课程数量不足2门，优先考虑线下课程。
            3. 已经满足要求的类别不需要重复优先推荐。
            4. 如果候选课程中没有对应类别，应如实说明。
            规则：
            1.只能推荐候选课程中真实存在的课程。
            2.只能返回候选课程中的课程ID。
            3.不得编造课程、教师或课程信息。
            4.优先考虑学生指定的教师、课程性质和授课方式。
            5.如果没有合适课程，返回空的courseIds数组。
            6.不要输出Markdown代码块，只返回JSON。
            7.学生询问无关问题涉一概不回答。 
            返回格式：
            {
              "reason": "推荐理由",
              "courseIds": [5, 6]
            }
            """.formatted(
                    request.getInterest(),
                    request.getLearningGoal(),
                    request.getTeacher(),
                    request.getCourseType(),
                    request.getTeachingMode(),
                    progress.getSocialCredits(),
                    progress.getNaturalCredits(),
                    progress.getArtCredits(),
                    progress.getEthnicCredits(),
                    progress.getCompletedOfflineCourses(),
                    progress.getPendingCourses(),
                    progress.getFailedCourses(),
                    requirements.getSocialSatisfied(),
                    requirements.getNaturalSatisfied(),
                    requirements.getArtSatisfied(),
                    requirements.getEthnicSatisfied(),
                    requirements.getOfflineSatisfied(),
                    courseJson
            );
            //调用DeepSeek
            String result = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            // 解析 AI 返回的 JSON
            AiRecommendResult aiResult = objectMapper.readValue(result, AiRecommendResult.class);
            //获取后端允许推荐的课程ID
            Set<Long> allowedIds = candidates.stream()
                    .map(Course::getId)
                    .collect(Collectors.toSet());
            //获取 AI 推荐的课程 ID
            List<Long> validIds = aiResult.getCourseIds();
            if (validIds == null) {
                validIds = new ArrayList<>();
            }
            // 校验课程 ID，过滤无效数据
            List<Long> verifiedIds = validIds.stream().filter(Objects::nonNull).filter(allowedIds::contains).distinct().limit(3).collect(Collectors.toList());
            // 根据验证后的 ID 获取真实课程信息
            List<Course> recommendedCourses = candidates.stream().filter(course -> verifiedIds.contains(course.getId())).collect(Collectors.toList());
            // 组装最终响应
            CourseRecommendResponse response = new CourseRecommendResponse();
            response.setReason(aiResult.getReason());
            response.setCourses(recommendedCourses);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("AI课程推荐失败：" + e.getMessage(), e);
        }
    }
}
