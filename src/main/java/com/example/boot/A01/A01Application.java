package com.example.boot.A01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class A01Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(A01Application.class);

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(A01Application.class, args);

        /**
         * 1. 什么是BeanFactory
         *  ApplicationContext 的父接口
         *  他是 ApplicationContext 的核心容器，主要的 ApplicationContext 实现都组合了他的功能
         */
//        context.getBean("aaa");

        /**
         * 2.BeanFactory 能干什么
         * 简单来看只有 getBean 的一些操作
         * 但其实控制反转，基本的依赖注入，直到Bean的生命周期管理的各种功能，都是由他的实现类提供
         */
        // 案例: 取出单例bean
        Field singletonObjects = DefaultSingletonBeanRegistry.class.getDeclaredField("singletonObjects");
        singletonObjects.setAccessible(true);
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        Map<String, Object> map = (Map<String, Object>) singletonObjects.get(beanFactory);
        map.entrySet().stream().filter(e -> e.getKey().startsWith("component")).forEach(System.out::println);

        /**
         * 3.ApplicationContext 比 BeanFactory 多的功能
         * MessageSource: i18n 国际化 后续请求头中提供
         * ResourcePatternResolver: 资源解析 通过通配符获取资源
         * EnvironmentCapable: 获取配置信息
         * ApplicationEventPublisher: 事件发布，组件解耦
         */
        System.out.println(context.getMessage("hi", null, Locale.CHINA));
        System.out.println(context.getMessage("hi", null, Locale.ENGLISH));

        Resource[] resources = context.getResources("classpath*:META-INF/spring.factories"); // 默认不会找jar中的文件需要加 *
        for (Resource r : resources) {
            System.out.println(r);
        }

        System.out.println(context.getEnvironment().getProperty("java_home"));
        System.out.println(context.getEnvironment().getProperty("server.port"));

//        context.publishEvent(new UserRegisteredEvent(context));
        Component1 bean = context.getBean(Component1.class);
        bean.register();

        context.close();

        /**
         * 学到了什么
         *  a. BeanFactory 和 ApplicationContext 并不是简单的接口继承关系，Application 组合并扩展了 BeanFactory 的功能
         *  b. 又新学了一种代码之间解耦途径
         */

    }
}
