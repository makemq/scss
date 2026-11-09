package com.example.scss.controller;
import com.example.scss.config.Result;
import com.example.scss.entity.User;
import com.example.scss.mapper.UserMapper;
import com.example.scss.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/user")
// @CrossOrigin // 如果遇到跨域报错，可以临时加上这个注解测试（推荐还是去 CorsConfig 里统一配好）
public class UserController {

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        System.out.println("前端传来的账号：" + user.getUsername());
        System.out.println("前端传来的密码：" + user.getPassword());

        // 假装验证成功，直接返回一个假 token（先不接 JwtUtil）
        return Result.success("登录成功，这是测试用的假Token");
    }
}