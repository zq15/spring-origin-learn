package com.example.boot.A01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Component2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Component2.class);


    @EventListener
    public void aaa(UserRegisteredEvent event) {
        LOGGER.info("{}", event);
        LOGGER.info("发送短信");
    }
}
