package com.example.scss;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.example.scss.mapper")
@EnableCaching
public class ScssApplication {

	public static void main(String[] args) {

		SpringApplication.run(ScssApplication.class, args);

	}
}
