package com.example.boot.A42;

import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

public class A42_1 {

    @SuppressWarnings("all")
    public static void main(String[] args) throws IOException {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("config", Config.class);
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.refresh();

        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        // Druid 存在，注入 bean1，否则注入 bean2
    }

    @Configuration // 本项目的配置类
    @Import(MyImportSelector.class)
    static class Config {
    }

    static class MyImportSelector implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{AutoConfiguration1.class.getName(), AutoConfiguration2.class.getName()};
        }
    }

    static class MyCondition1 implements Condition { // 存在 Druid 依赖
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return ClassUtils.isPresent("com.alibaba.druid.pool.DruidDataSource", null);
        }
    }

    static class MyCondition2 implements Condition { // 不存在 Druid 依赖
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return !ClassUtils.isPresent("com.alibaba.druid.pool.DruidDataSource", null);
        }
    }

    @Configuration // 第三方的配置类
    @Conditional(MyCondition1.class)
    static class AutoConfiguration1 {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }
    }

    @Configuration // 第三方的配置类
    @Conditional(MyCondition2.class)
    static class AutoConfiguration2 {
        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }
    }

    static class Bean1 {

    }

    static class Bean2 {

    }
}

