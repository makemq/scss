package com.example.scss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.scss.entity.Student;
import com.example.scss.mapper.StudentMapper;
import com.example.scss.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;




@Service
public class StudentServiceImpl implements StudentService {
    private final StudentMapper studentMapper;

    public StudentServiceImpl(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @Override
    public List<Student> listStudents() {
        return studentMapper.selectList(null);
    }

    @Override
    public Student getStudentById(Long id) {
        return studentMapper.selectById(id);
    }
    // 新增学生
    @Override
    public boolean addStudent(Student student) {

        // 检查学号是否已经存在
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNo, student.getStudentNo());

        Long count = studentMapper.selectCount(wrapper);

        // 如果学号已经存在，不允许新增
        if (count > 0) {
            return false;
        }

        // 保存学生信息
        return studentMapper.insert(student) > 0;
    }

    // 修改学生信息
    @Override
    public boolean updateStudent(Student student) {

        // 1. 检查学生是否存在
        Student oldStudent = studentMapper.selectById(student.getId());

        if (oldStudent == null) {
            return false;
        }

        // 2. 检查学号是否被其他学生使用
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Student::getStudentNo, student.getStudentNo())
                .ne(Student::getId, student.getId());

        Long count = studentMapper.selectCount(wrapper);

        if (count > 0) {
            return false;
        }

        // 3. 执行修改
        return studentMapper.updateById(student) > 0;
    }
    // 根据 ID 删除学生
    @Override
    public boolean deleteStudent(Long id) {

        // 1. 检查学生是否存在
        Student student = studentMapper.selectById(id);

        if (student == null) {
            return false;
        }

        // 2. 删除学生
        return studentMapper.deleteById(id) > 0;
    }
}
