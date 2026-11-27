
package com.example.scss.service.impl;
import org.springframework.cache.annotation.CacheEvict;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scss.entity.Course;
import com.example.scss.mapper.CourseMapper;
import com.example.scss.service.CourseService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.example.scss.mapper.EnrollmentMapper;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseMapper courseMapper;
    private final EnrollmentMapper enrollmentMapper;
    public CourseServiceImpl(CourseMapper courseMapper, EnrollmentMapper enrollmentMapper) {
        this.courseMapper = courseMapper;
        this.enrollmentMapper = enrollmentMapper;
    }
    @Override
    @Cacheable(value = "courses",key = "'list'")
    public List<Course> listCourses() {
        return courseMapper.selectList(null);
    }
    @Override
    @Cacheable(value = "courses", key = "'detail:' + #id")
    public Course getCourseById(Long id) {
        return courseMapper.selectById(id);
    }
    @Override
    @CacheEvict(value = "courses", allEntries = true)
    public boolean addCourse(Course course) {
        //初始化
        if(course.getSelectedCount()==null){
            course.setSelectedCount(0);
        }
        //课程状态1正常
        if(course.getStatus()== null){
            course.setStatus(1);
        }
        return courseMapper.insert(course) > 0;
    }
    @Override
    @CacheEvict(value = "courses", allEntries = true)
    public boolean updateCourse(Course course) {
        //检查课程是否存在
        Course oldCourse = courseMapper.selectById(course.getId());
        if (oldCourse == null) {
            return false;
        }
        return courseMapper.updateById(course) > 0;
    }
    @Override
    @Transactional//将删除操作添加到事务中
    @CacheEvict(value = "courses", allEntries = true)
    public boolean deleteCourse(Long id) {
        //查询课程时加数据库行锁，避免并发操作造成数据问题
        Course course = courseMapper.selectByIdForUpdate(id);
        if (course == null) {
            return false;
        }
        //检查该课程是否还有有效选课记录
        Long count = enrollmentMapper.countByCourseId(id);
        if (count != null && count > 0) {
            return false;
        }
        return courseMapper.deleteById(id) > 0;
    }
    @Override
    public IPage<Course> pageCourses(String keyword, Long pageNum, Long pageSize) {
        //创建分页对象
        Page<Course> page = new Page<>(pageNum, pageSize);
        //创建查询条件
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        //按课程与老师姓名查询
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = keyword.trim();
            wrapper.and(w -> w
                    .like(Course::getCourseName, searchKeyword)
                    .or()
                    .like(Course::getTeacher, searchKeyword)
            );
        }

        return courseMapper.selectPage(page,wrapper);
    }
}

