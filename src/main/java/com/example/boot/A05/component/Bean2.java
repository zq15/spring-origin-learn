package com.example.boot.A05.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Bean2 {
    public Bean2() {
        log.info("Bean2 被创建啦");
    }
}
