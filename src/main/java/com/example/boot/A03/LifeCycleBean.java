package com.example.boot.A03;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class LifeCycleBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(LifeCycleBean.class);

    public LifeCycleBean() {
        LOGGER.info("构造");
    }

    @Autowired
    public void autowire(@Value("${JAVA_HOME}") String home) {
        LOGGER.info("依赖注入: {}", home);
    }

    @PostConstruct
    public void init() {
        LOGGER.info("初始化");
    }

    @PreDestroy
    public void destroy() {
        LOGGER.info("销毁");
    }

}
