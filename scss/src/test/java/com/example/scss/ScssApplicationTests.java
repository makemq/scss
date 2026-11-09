package com.example.scss;

import com.example.scss.config.Result;
import com.example.scss.entity.User;
import com.example.scss.mapper.UserMapper;
import com.example.scss.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class ScssApplicationTests {

	@Test
	void contextLoads() {
	}

}
@RestController
@RequestMapping("/api/auth")
 class UserController {
	@Autowired
	private UserMapper userMapper;
	@Autowired private JwtUtil jwtUtil;

	// 使用 BCrypt 进行密码加密
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	// 注册
	@PostMapping("/register")
	public Result<?> register(@RequestBody User user) {
		// 1. 检查用户名是否存在... (略)
		// 2. 密码加密存储
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userMapper.insert(user);
		return Result.success("注册成功");
	}

	// 登录
	@PostMapping("/login")
	public Result<Map<String, String>> login(@RequestBody User user) {
		User dbUser = userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>().eq("username", user.getUsername()));
		if (dbUser == null || !passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
			return Result.error("用户名或密码错误");
		}
		// 签发 Token
		String token = jwtUtil.generateToken(dbUser.getUsername());
		Map<String, String> data = new HashMap<>();
		data.put("token", token);
		return Result.success(data);
	}
}
