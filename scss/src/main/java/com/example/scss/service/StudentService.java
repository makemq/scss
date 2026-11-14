package com.example.scss.service;
import com.example.scss.entity.Student;

import java.util.List;

public interface StudentService {

    // 查询所有学生mvn clean compile
    List<Student> listStudents();

    // 根据 ID 查询学生
    Student getStudentById(Long id);
    boolean addStudent(Student student);
    boolean updateStudent(Student student);
    boolean deleteStudent(Long id);
}