package com.example.scss.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scss.config.Result;
import com.example.scss.dto.StudentAddRequest;
import com.example.scss.dto.StudentUpdateRequest;
import com.example.scss.entity.Student;
import com.example.scss.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // 查询所有学生
    @GetMapping("/list")
    public Result<List<Student>> listStudents() {
        List<Student> students = studentService.listStudents();
        return Result.success(students);
    }

    // 根据 ID 查询学生
    @GetMapping("/{id}")
    public Result<Student> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);

        if (student == null) {
            return Result.error(404, "学生不存在");
        }

        return Result.success(student);
    }
    // 新增学生
    @PostMapping("/add")
    public Result<String> addStudent(@RequestBody StudentAddRequest request) {

        // 检查参数
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return Result.error(400, "学生姓名不能为空");
        }

        if (request.getStudentNo() == null || request.getStudentNo().trim().isEmpty()) {
            return Result.error(400, "学号不能为空");
        }

        // 创建学生对象
        Student student = new Student();
        student.setName(request.getName().trim());
        student.setStudentNo(request.getStudentNo().trim());

        // 调用业务层新增学生
        boolean success = studentService.addStudent(student);

        if (!success) {
            return Result.error(400, "学号已存在或新增失败");
        }

        return Result.success("学生新增成功");
    }
    // 修改学生信息
    @PutMapping("/update")
    public Result<String> updateStudent(
            @RequestBody StudentUpdateRequest request) {

        // 1. 检查 ID
        if (request.getId() == null) {
            return Result.error(400, "学生ID不能为空");
        }

        // 2. 检查姓名
        if (request.getName() == null ||
                request.getName().trim().isEmpty()) {
            return Result.error(400, "学生姓名不能为空");
        }

        // 3. 检查学号
        if (request.getStudentNo() == null ||
                request.getStudentNo().trim().isEmpty()) {
            return Result.error(400, "学号不能为空");
        }

        // 4. 创建学生对象
        Student student = new Student();
        student.setId(request.getId());
        student.setName(request.getName().trim());
        student.setStudentNo(request.getStudentNo().trim());

        // 5. 调用业务层
        boolean success = studentService.updateStudent(student);

        if (!success) {
            return Result.error(400, "学生不存在、学号重复或修改失败");
        }

        return Result.success("学生信息修改成功");
    }
    // 根据 ID 删除学生
    @DeleteMapping("/{id}")
    public Result<String> deleteStudent(@PathVariable Long id) {

        // 检查 ID
        if (id == null || id <= 0) {
            return Result.error(400, "学生ID不合法");
        }

        // 调用业务层删除
        boolean success = studentService.deleteStudent(id);

        if (!success) {
            return Result.error(404, "学生不存在或删除失败");
        }

        return Result.success("学生删除成功");
    }

    // 分页查询 + 关键词模糊搜索
    @Override
    public IPage<Student> pageStudents(
            String keyword,
            Long pageNum,
            Long pageSize) {

        // 1. 创建分页对象
        Page<Student> page = new Page<>(pageNum, pageSize);

        // 2. 创建查询条件
        LambdaQueryWrapper<Student> wrapper =
                new LambdaQueryWrapper<>();

        // 3. 判断关键词是否为空
        if (keyword != null && !keyword.trim().isEmpty()) {

            String searchKeyword = keyword.trim();

            // 姓名或学号包含关键词即可匹配
            wrapper.and(w -> w
                    .like(Student::getName, searchKeyword)
                    .or()
                    .like(Student::getStudentNo, searchKeyword)
            );
        }

        // 4. 执行分页查询
        return studentMapper.selectPage(page, wrapper);
    }
}