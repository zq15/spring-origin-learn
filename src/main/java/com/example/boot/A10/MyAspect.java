package com.example.boot.A10;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect // 切面没有被 spring 管理
@Slf4j
//@Component
public class MyAspect {

    @Before("execution(* com.example.boot.A10.MyService.foo())")
    public void before() {
        log.info("before");
    }
}
