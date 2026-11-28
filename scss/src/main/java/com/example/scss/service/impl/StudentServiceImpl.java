package com.example.scss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scss.entity.Student;
import com.example.scss.mapper.StudentMapper;
import com.example.scss.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class StudentServiceImpl implements StudentService {
    public StudentServiceImpl(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }
    private final StudentMapper studentMapper;

    //查询
    @Override
    public List<Student> listStudents() {
        return studentMapper.selectList(null);
    }//id查询
    @Override
    public Student getStudentById(Long id) {
        return studentMapper.selectById(id);
    }//增加
    @Override
    public boolean addStudent(Student student) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNo, student.getStudentNo());
        Long count = studentMapper.selectCount(wrapper);
        if(count > 0){
            return false;
        }
        return studentMapper.insert(student) > 0;
    }//更新
    @Override
    public boolean updateStudent(Student student) {
        Student oldStudent = studentMapper.selectById(student.getId());
        if(oldStudent == null ){
            return false;
        }
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNo, student.getStudentNo()).ne(Student::getId, student.getId());
        Long count = studentMapper.selectCount(wrapper);
        if(count > 0){
            return false;
        }
        return studentMapper.updateById(student)> 0;
    }//删除
    @Override
    public boolean deleteStudent(Long id) {
        Student student = studentMapper.selectById(id);
        if(student == null){
            return false;
        }
        return studentMapper.deleteById(id) > 0;
    }//分页查询+模糊搜索
    @Override
    public IPage<Student> pageStudents(String keyword, Long pageNum, Long pageSize) {
        Page<Student> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        if(keyword != null && !keyword.trim().isEmpty()){
            String searchKeyword = keyword.trim();
        wrapper.and(w->w.like(Student::getName,searchKeyword).or().like(Student::getStudentNo,searchKeyword)
        );
        }
        return studentMapper.selectPage(page,wrapper);
    }
}



