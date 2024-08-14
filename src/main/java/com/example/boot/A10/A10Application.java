package com.example.boot.A10;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@Slf4j
@EnableAspectJAutoProxy
public class A10Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(A10Application.class, args);
        MyService myService = context.getBean(MyService.class);

        log.info("service class: {}", myService.getClass()); // service class: class com.example.boot.A10.MyService

        myService.foo();

        context.close();
    }
}
