package com.example.scss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.scss.entity.SysUser;
import com.example.scss.mapper.UserMapper;
import com.example.scss.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public SysUser findByUsername(String username) {
        if(username==null|| username.isBlank()){
            return null;
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public SysUser register(String username, String rawPassword) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (username.length() > 50) {
            throw new IllegalArgumentException("用户名不能超过50个字符");
        }
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException("密码至少需要8个字符");
        }
        if (rawPassword.length() > 72) {
            throw new IllegalArgumentException("密码不能超过72个字符");
        }
        if (findByUsername(username) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("STUDENT");
        userMapper.insert(user);
        return user;
    }
    @Override
    public boolean verifyPassword(
            String username,
            String rawPassword) {

        if (rawPassword == null || rawPassword.isEmpty()) {
            return false;
        }

        SysUser user = findByUsername(username);

        if (user == null || user.getPassword() == null) {
            return false;
        }

        return passwordEncoder.matches(
                rawPassword,
                user.getPassword()
        );
    }

    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        // 1. 检查新密码
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("新密码至少需要8个字符");
        }
        if (newPassword.length() > 72) {
            throw new IllegalArgumentException("新密码不能超过72个字符");
        }
        // 2. 查询用户
        SysUser user = findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        // 3. 验证旧密码
        if (oldPassword == null ||
                !passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }
        // 4. 加密新密码
        user.setPassword(passwordEncoder.encode(newPassword));

        // 5. 更新数据库
        return userMapper.updateById(user) > 0;
    }

}
