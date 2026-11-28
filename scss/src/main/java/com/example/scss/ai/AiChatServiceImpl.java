package com.example.scss.ai;

import com.example.scss.dto.AiChatResponse;
import com.example.scss.dto.StudyProgressResponse;
import com.example.scss.entity.Course;
import com.example.scss.mapper.CourseMapper;
import com.example.scss.mapper.EnrollmentMapper;
import com.example.scss.service.StudyProgressService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AiChatServiceImpl implements AiChatService {
    private final ChatClient chatClient;
    private final CourseMapper courseMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final StudyProgressService studyProgressService;
    private final ObjectMapper objectMapper;
    public AiChatServiceImpl(ChatClient.Builder builder, CourseMapper courseMapper, EnrollmentMapper enrollmentMapper, StudyProgressService studyProgressService, ObjectMapper objectMapper) {
        this.chatClient = builder.build();
        this.courseMapper = courseMapper;
        this.enrollmentMapper = enrollmentMapper;
        this.studyProgressService = studyProgressService;
        this.objectMapper = objectMapper;
    }
    @Override
    public AiChatResponse chat(Long studentId, String message) {
        StudyProgressResponse progress = studyProgressService.getStudyProgress(studentId);
        List<Course> candidates = courseMapper.selectAvailableElectiveCourses(
                        null, null, null
                );
        Set<Long> enrolledIds = new HashSet<>(enrollmentMapper.selectStudentEnrolledCourseIds(studentId)
        );
        List<Course> schedule = enrollmentMapper.selectStudentSchedule(studentId);

        candidates.removeIf(course -> enrolledIds.contains(course.getId())
        );

        candidates.removeIf(candidate -> schedule.stream().anyMatch(selected -> candidate.getClassDay() != null
                                && candidate.getClassDay().equals(selected.getClassDay())
                                && candidate.getStartSection() != null
                                && candidate.getEndSection() != null
                                && selected.getStartSection() != null
                                && selected.getEndSection() != null
                                && candidate.getStartSection() <= selected.getEndSection()
                                && candidate.getEndSection() >= selected.getStartSection()
                )
        );

        try {

            String progressJson = objectMapper.writeValueAsString(progress);
            String courseJson = objectMapper.writeValueAsString(candidates);
            String prompt = """
                    你是大学学生选课系统中的 AI 智能选课助手。

                    你的职责：
                    1. 解答学生关于选课、课程、学分和修读要求的问题。
                    2. 根据学生真实修读进度提供建议。
                    3. 推荐课程时，只能推荐下面真实存在且当前可选的课程。
                    4. 不得编造课程、教师、学分或课程信息。
                    5. 不得替学生执行选课、退课等操作。
                    6. 不回答与大学选课和学习规划无关的问题。
                    7. 如果当前没有合适课程，应如实说明。

                    学校选修课修读要求：
                    - 社科至少获得1学分。
                    - 自然至少获得1学分。
                    - 艺体至少获得1学分。
                    - 民族至少获得1学分。
                    - 至少完成2门线下课程。
                    - 只有成绩达到60分的有效选修课才算完成。
                    - 待出成绩和未通过的课程不算完成。

                    学生当前修读进度：
                    %s

                    当前真实可选课程：
                    %s

                    学生的问题：
                    %s

                    请使用简体中文回答。
                    回答应清晰、具体，并优先结合学生的实际数据。
                    """.formatted(progressJson, courseJson, message
            );

            String answer = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            AiChatResponse response = new AiChatResponse();
            response.setAnswer(answer);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(
                    "AI对话失败：" + e.getMessage(), e
            );
        }
    }
}
