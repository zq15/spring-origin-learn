package com.example.boot.A39;

import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.lang.reflect.Constructor;
import java.util.List;

// 1. 创建事件发布器
public class A39_2 {
    public static void main(String[] args) throws Exception {
        // 添加 app 监听器
        SpringApplication app = new SpringApplication();
        app.addListeners(event -> System.out.println(event.getClass()));

        // 获取事件发送器实现类名称
        List<String> names = SpringFactoriesLoader.loadFactoryNames(SpringApplicationRunListener.class, A39_2.class.getClassLoader());
        for (String name : names) {
            System.out.println(name);
            Class<?> clazz = Class.forName(name);
            Constructor<?> constructor = clazz.getConstructor(SpringApplication.class, String[].class);
            SpringApplicationRunListener publisher = (SpringApplicationRunListener) constructor.newInstance(app, args);
            // 发布事件
            DefaultBootstrapContext defaultBootstrapContext = new DefaultBootstrapContext();
            publisher.starting(defaultBootstrapContext); // springboot开始启动
            publisher.environmentPrepared(defaultBootstrapContext, new StandardEnvironment()); // 环境信息准备完毕
            GenericApplicationContext context = new GenericApplicationContext();
            publisher.contextPrepared(context); // spring 容器创建，并调用初始化器之后，发送此事件
            publisher.contextLoaded(context); // 所有 bean definition 加载完毕
            context.refresh();
            publisher.started(context, null); // spring 容器初始化完成(refresh)

            publisher.ready(context, null); // springboot 启动完毕

            publisher.failed(context, new Exception("出错了")); // springboot 启动出错
        }
    }
}
