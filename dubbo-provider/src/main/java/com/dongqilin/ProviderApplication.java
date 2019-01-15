package com.dongqilin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @description:
 * @author: dongql
 * @date: 2018/3/27 9:31
 */

@SpringBootApplication
@ImportResource("classpath:provider.xml")
public class ProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }
}
