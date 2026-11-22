package com.example.scss.controller;
import com.example.scss.annotation.RequireRole;
import com.example.scss.config.Result;
import com.example.scss.dto.RefreshRequest;
import com.example.scss.dto.RegisterRequest;
import com.example.scss.entity.SysUser;
import com.example.scss.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.scss.dto.LoginRequest;
import com.example.scss.service.UserService;
import com.example.scss.utils.JwtUtil;
import com.example.scss.dto.UserProfileResponse;
import com.example.scss.entity.Student;
import com.example.scss.mapper.StudentMapper;
@RestController
@RequestMapping("/user")
// @CrossOrigin //
public class UserController {
    @Autowired
    private StudentMapper studentMapper;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    @RequireRole("ADMIN")
    @GetMapping("/admin/test")
    public Result adminTest() {
        return Result.success("管理员权限验证成功");
    }
    @PostMapping("/register")
    public Result register(@RequestBody RegisterRequest request) {
        if (request == null) {
            return Result.error(400, "注册信息不能为空");
        }
        if (request.getUsername() == null
                || request.getUsername().isBlank()) {
            return Result.error(400, "用户名不能为空");
        }
        if (request.getPassword() == null
                || request.getPassword().isBlank()) {
            return Result.error(400, "密码不能为空");
        }
        try {
            userService.register(
                    request.getUsername(),
                    request.getPassword()
            );
            return Result.success("注册成功");
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        }
    }
    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request) {
        //检查登录
        if (request == null
                || request.getUsername() == null
                || request.getUsername().isBlank()
                || request.getPassword() == null
                || request.getPassword().isBlank()) {
            return Result.error(400, "用户名和密码不能为空");
        }
        // 验证用户名和密码
        boolean valid = userService.verifyPassword(
                request.getUsername(),
                request.getPassword()
        );

        if (!valid) {
            return Result.error(401, "用户名或密码错误");
        }
        // 生成 JWT Token
        String accessToken = jwtUtil.generateAccessToken(
                request.getUsername()
        );
        String refreshToken = jwtUtil.generateRefreshToken(
                request.getUsername()
        );
        return Result.success(java.util.Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }
    }
    @PostMapping("//refresh")
    public Result refresh(@RequestBody RefreshRequest request) {
        if (request == null
                || request.getRefreshToken() == null
                || request.getRefreshToken().isBlank()) {
            return Result.error(400, "Refresh Token 不能为空");
        }
        //获取token
        String refreshToken = request.getRefreshToken();
        String tokenType;
        String username;
        try {
            tokenType = jwtUtil.getTokenType(refreshToken);
            username = jwtUtil.getUsernameFromToken(refreshToken);
        } catch (Exception e) {
            return Result.error(401, "Refresh Token 无效或已过期");
        }
        //验证token
        if (!"REFRESH".equals(tokenType)) {
            return Result.error(401, "Token 类型错误");
        }
        SysUser user = userService.findByUsername(username);
        if (user == null) {
            return Result.error(401, "用户不存在");
        }
        //生成新的访问token
        String newAccessToken = jwtUtil.generateAccessToken(username);
        return Result.success(java.util.Map.of(
                "accessToken", newAccessToken
        ));

    }
    @GetMapping("/me")
    public Result<UserProfileResponse> me(HttpServletRequest request) {

        String username = (String) request.getAttribute("username");

        if (username == null) {
            return Result.error("用户未登录");
        }

        SysUser user = userService.findByUsername(username);

        if (user == null) {
            return Result.error("用户不存在");
        }

        UserProfileResponse profile = new UserProfileResponse();
        profile.setUsername(user.getUsername());
        profile.setRole(user.getRole());
        profile.setStudentId(user.getStudentId());

        // 如果是学生账号，查询对应的学生档案
        if ("STUDENT".equals(user.getRole()) && user.getStudentId() != null) {
            Student student = studentMapper.selectById(user.getStudentId());

            if (student != null) {
                profile.setName(student.getName());
                profile.setStudentNo(student.getStudentNo());
            }
        }

        return Result.success(profile);
    }


}