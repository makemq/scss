package com.example.scss.controller;

import com.example.scss.config.Result;
import com.example.scss.dto.StudentAddRequest;
import com.example.scss.dto.StudentUpdateRequest;
import com.example.scss.entity.Student;
import com.example.scss.service.StudentService;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import com.example.scss.annotation.RequireRole;
@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    //查詢全部學生
    @RequireRole("ADMIN")
    @GetMapping("/list")
    public Result<List<Student>>listStudents(){
        List<Student> students = studentService.listStudents();
        return Result.success(students);
    }//根据id查找學生
    @RequireRole("ADMIN")
    @GetMapping("/{id}")
    public Result<Student>getStudentById(@PathVariable Long id){
        Student student = studentService.getStudentById(id);
        if(student ==null){
            return Result.error(404,"學生不存在");
        }
        return Result.success(student);
    }//新增學生
    @RequireRole("ADMIN")
    @PostMapping("/add")
    public Result<String> addStudent(@RequestBody StudentAddRequest request){
    if(request.getName()==null||request.getName().trim().isEmpty()){
        return Result.error(400,"學生姓名不能为空");
    }
    if(request.getStudentNo()==null||request.getStudentNo().trim().isEmpty()){
        return Result.error(400,"學生学号不能为空");
    }
    Student student = new Student();
    student.setName(request.getName().trim());
    student.setStudentNo(request.getStudentNo().trim());
    boolean success = studentService.addStudent(student);
    if(!success){
        return Result.error(400,"学号已存在或新增失败");
    }
    return Result.success("学生新增成功");
    }//修改学生信息
    @RequireRole("ADMIN")
    @PutMapping("/update")
    public Result<String> updateStudent(@RequestBody StudentUpdateRequest request){
    if(request.getId()==null){
    return Result.error(400,"学生ID不能为空");
}
    if(request.getName()==null||request.getName().trim().isEmpty()){
            return Result.error(400,"學生姓名不能为空");
        }
    if(request.getStudentNo()==null||request.getStudentNo().trim().isEmpty()){
            return Result.error(400,"學生学号不能为空");
        }
    Student student = new Student();
    student.setId(request.getId());
    student.setName(request.getName().trim());
    student.setStudentNo(request.getStudentNo().trim());
    boolean success = studentService.updateStudent(student);
    if(!success){
            return Result.error(400,"学生不存在或修改失败");
        }
        return Result.success("学生修改成功");
    }//删除学生
    @RequireRole("ADMIN")
    @DeleteMapping("/{id}")
    public Result<String> deleteStudent(@PathVariable Long id){
        boolean success = studentService.deleteStudent(id);
        if(!success){
            return Result.error(400,"学生不存在或删除失败");
        }
        return Result.success("学生删除成功");
    }//分页查询与模糊搜索
    @RequireRole("ADMIN")
    @GetMapping("/page")
    public Result<IPage<Student>> pageStudents(@RequestParam(required = false ) String keyword,
                                               @RequestParam(defaultValue = "1") Long pageNum,
                                               @RequestParam(defaultValue = "10") Long pageSize) {
        if (pageNum < 1 || pageSize < 1 || pageSize > 100) {
            return Result.error(400, "分页参数不合法");
        }
        IPage<Student> result = studentService.pageStudents(keyword, pageNum, pageSize);
        return Result.success(result);
    }


}


