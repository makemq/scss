package com.example.scss.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private String username;
    private String role;
    private Long studentId;
    private String studentNo;
    private String name;
}
