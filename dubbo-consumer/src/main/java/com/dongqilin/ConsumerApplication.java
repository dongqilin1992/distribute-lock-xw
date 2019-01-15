package com.dongqilin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @description:
 * @author: dongql
 * @date: 2018/3/27 10:09
 */
@SpringBootApplication
@ImportResource("classpath:consumer.xml")
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
