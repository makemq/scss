package com.example.scss.controller;

import com.example.scss.config.Result;
import com.example.scss.entity.Student;
import com.example.scss.service.StudentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }//查詢全部學生
    @GetMapping("/list")
    public Result<List<Student>>listStudents(){
        List<Student> students = studentService.listStudents();
        return Result.success(students);
    }//
    @GetMapping("/{id}")
    public Result<Student>getStudentById(@PathVariable Long id){
        Student student = studentService.getStudentById(id);
        if(student ==null){
            return Result.error(404,"學生不存在");
        }
        return Result.success(student);
    }

}
