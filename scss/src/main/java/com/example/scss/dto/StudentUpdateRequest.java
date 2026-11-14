package com.example.scss.dto;

import lombok.Data;

@Data
public class StudentUpdateRequest {

    private Long id;

    private String name;

    private String studentNo;
}