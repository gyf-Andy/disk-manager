package com.diskmanager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.diskmanager.mapper")
public class DiskManagerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DiskManagerApplication.class, args);
    }
}
