package com.example.boot.A10;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MyService {
    public void foo() {
        log.info("foo()");
        bar();
    }

    public void bar() {
        log.info("bar()");
    }
}
