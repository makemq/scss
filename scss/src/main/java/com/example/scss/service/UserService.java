package com.example.scss.service;
import com.example.scss.dto.RegisterRequest;
import com.example.scss.service.UserService;
import com.example.scss.entity.SysUser;

public interface UserService {

    SysUser findByUsername(String username);
    SysUser register(String username, String rawPassword);
    boolean verifyPassword(String username, String rawPassword);
    boolean changePassword(String username, String oldPassword, String newPassword);
}
