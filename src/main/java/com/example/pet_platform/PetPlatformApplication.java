package com.example.pet_platform;

import com.example.pet_platform.component.WebAdminSocketServer;
import com.example.pet_platform.component.WebSocketServer;
import com.example.pet_platform.controller.MessageController;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
@EnableCaching
@MapperScan("com.example.pet_platform.mapper")
public class PetPlatformApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(PetPlatformApplication.class, args);
        WebSocketServer.setApplicationContext(run);
        WebAdminSocketServer.setApplicationContext(run);
    }

}
