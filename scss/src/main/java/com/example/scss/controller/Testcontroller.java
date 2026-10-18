package com.example.scss.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Testcontroller {

    @GetMapping("/test")
    public String test() {
        return "基础框架搭建成功，后端已启动！";
    }
}
