package com.example.boot.A35;

import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;

public class A35 {
    public static void main(String[] args) {
        AnnotationConfigServletWebServerApplicationContext context = 
            new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);

    }
}
