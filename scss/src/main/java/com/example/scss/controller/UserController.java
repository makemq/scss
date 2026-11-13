package com.example.scss.controller;
import com.example.scss.config.Result;
import com.example.scss.dto.RegisterRequest;
import com.example.scss.entity.SysUser;
import com.example.scss.service.UserService;
import org.springframework.web.bind.annotation.*;
import com.example.scss.dto.LoginRequest;
import com.example.scss.service.UserService;
import com.example.scss.utils.JwtUtil;
@RestController
@RequestMapping("/user")
// @CrossOrigin // 如果遇到跨域报错，可以临时加上这个注解测试（推荐还是去 CorsConfig 里统一配好）
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    @PostMapping("/register")
    public Result register(@RequestBody RegisterRequest request) {

        if (request == null) {
            return Result.error("注册信息不能为空");
        }

        try {
            userService.register(
                    request.getUsername(),
                    request.getPassword()
            );

            return Result.success("注册成功");

        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }
    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request) {

        if (request == null
                || request.getUsername() == null
                || request.getPassword() == null) {
            return Result.error("用户名和密码不能为空");
        }

        // 验证用户名和密码
        boolean valid = userService.verifyPassword(
                request.getUsername(),
                request.getPassword()
        );

        if (!valid) {
            return Result.error("用户名或密码错误");
        }

        // 验证成功，生成 JWT Token
        String token = jwtUtil.generateToken(
                request.getUsername()
        );

        return Result.success(token);
    }
}