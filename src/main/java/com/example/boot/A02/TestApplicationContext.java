package com.example.boot.A02;

import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.Controller;

public class TestApplicationContext {
    public static void main(String[] args) {
//        testClassPathXmlApplicationContext();
//        testFIleSystemXmlApplicationContext();

        // 手动处理xml配置文件读取
//        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
//        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
//            System.out.println(beanDefinitionName);
//        }
//        System.out.println(">>>>>>>>>>>>>>>>>>读取后");

//        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
//        reader.loadBeanDefinitions(new ClassPathResource("b01.xml"));
//        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
//            System.out.println(beanDefinitionName);
//        }

//        testAnnotationConfigApplicationContext();
        testAnnotationConfigServletWebServerApplicationContext();

        /**
         * 学到了什么
         * 1. ApplicationContext 常见实现
         * 2. 内嵌 tomcat和 dispatcher 使用方式
         */
    }

    // 较为经典的容器，基于 classpath 下 xml 格式的配置文件来创建
    public static void testClassPathXmlApplicationContext(){
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("b01.xml");

        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }

        System.out.println(context.getBean(Bean2.class).getBean1());
    }

    // 基于磁盘路径下的 xml 格式的配置文件来创建
    public static void testFIleSystemXmlApplicationContext() {
        FileSystemXmlApplicationContext context =
                new FileSystemXmlApplicationContext("D:\\github\\springboot-origin-lab\\src\\main\\resources\\b01.xml");

        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }

        System.out.println(context.getBean(Bean2.class).getBean1());
    }

    // 较为经典的容器，基于 java 配置类来创建
    public static void testAnnotationConfigApplicationContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }

        System.out.println(context.getBean(Bean2.class).getBean1());
    }

    // 较为经典的实现，java配置类，用于web环境
    public static void testAnnotationConfigServletWebServerApplicationContext() {
        AnnotationConfigServletWebServerApplicationContext context =
                new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
    }

    @Configuration
    static class WebConfig{
        @Bean
        public ServletWebServerFactory servletWebServerFactory() {
            return new TomcatServletWebServerFactory();
        }

        @Bean
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
        }

        // 把 dispatcherServlet 注册到 Servlet
        @Bean
        public DispatcherServletRegistrationBean registrationBean(DispatcherServlet dispatcherServlet) {
            return new DispatcherServletRegistrationBean(dispatcherServlet, "/");
        }
        @Bean("/hello") // bean 的名称为 / 开头就会被解析成 匹配路径
        public Controller controller1() {
            return (request, response) -> {
                response.getWriter().print("hello");
                return null;
            };
        }
    }

    @Configuration
    static class Config {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2(Bean1 bean1) {
            Bean2 bean2 = new Bean2();
            bean2.setBean1(bean1);
            return bean2;
        }
    }

    static class Bean1 {

    }

    static class Bean2 {
        private Bean1 bean1;

        public Bean1 getBean1() {
            return bean1;
        }

        public void setBean1(Bean1 bean1) {
            this.bean1 = bean1;
        }
    }
}
