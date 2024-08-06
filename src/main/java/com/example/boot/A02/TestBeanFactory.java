package com.example.boot.A02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

public class TestBeanFactory {
    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 1.添加 Bean 的定义 (class, scope, 初始化，销毁)
        AbstractBeanDefinition beanDefinition =
                BeanDefinitionBuilder.genericBeanDefinition(Config.class).setScope("singleton").getBeanDefinition();
        beanFactory.registerBeanDefinition("config", beanDefinition);

        // 2.给 BeanFactory 添加常见的后处理器
        /**
         * 2.1 beanFactory 后处理器 补充bean定义
         * org.springframework.context.annotation.internalConfigurationAnnotationProcessor 处理配置类中定义的bean
         * 2.2 bean 后处理器,bean 生命周期各个阶段提供扩展，@Autowires, @Resource 等的解析
         * org.springframework.context.annotation.internalAutowiredAnnotationProcessor
         * org.springframework.context.annotation.internalCommonAnnotationProcessor
         *
         * org.springframework.context.event.internalEventListenerProcessor
         * org.springframework.context.event.internalEventListenerFactory
         */
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);

        // 拿到所有bean工厂的后置处理器执行
        beanFactory.getBeansOfType(BeanFactoryPostProcessor.class).values().forEach(beanFactoryPostProcessor -> {
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        });

        // bean 后置处理器
        beanFactory.getBeansOfType(BeanPostProcessor.class).values().stream().sorted(beanFactory.getDependencyComparator()).forEach(beanPostProcessor -> {
            // 可以验证默认先加载 Autowired，先加载beanPostProcessor的生效
            // 添加比较器以后，会先加载 Common、
            System.out.println(">>>>>>>>>>>>>>>>>" + beanPostProcessor);
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        });

        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName); // 只打印 config，缺少解析注解能力
        }

        // 3. 单例默认延迟实例化，我们调用方法提前准备好所有的单例
        beanFactory.preInstantiateSingletons();
        System.out.println(">>>>>>>>>>>>>");

        Bean1 bean1 = beanFactory.getBean(Bean1.class);
//        System.out.println(bean1.getBean2()); // 默认不注入，需要调用 bean 后置处理器来完成注入
        System.out.println(bean1.getInter()); //Bean3
        /**
         * 学到了什么
         * a. BeanFactory 不会做的事情
         *  1. 不会主动调用 BeanFactory 后置处理器
         *  2. 不会主动添加 Bean 后置处理器
         *  3. 不会初始化单例
         *  4. 不会解析 BeanFactory ${} 和 #{} el表达式 略
         * b. bean 后处理器有排序的逻辑
         */
    }

    @Configuration
    static class Config {
        @Bean
        public Bean1 bean1(){
            return new Bean1();
        }

        @Bean
        public Bean2 bean2(){
            return new Bean2();
        }

        @Bean
        public Bean3 bean3(){
            return new Bean3();
        }

        @Bean
        public Bean4 bean4(){
            return new Bean4();
        }
    }

    interface Inter {
    }

    static class Bean3 implements Inter {

    }

    static class Bean4 implements Inter {

    }

    static class Bean1 {
        private static final Logger LOGGER = LoggerFactory.getLogger(Bean1.class);

        public Bean1() {
            LOGGER.info("构造bean1");
        }

        @Autowired
        private Bean2 bean2;

        @Autowired
        @Resource(name = "bean4")
        private Inter bean3;

        public Inter getInter() {
            return bean3;
        }

        public Bean2 getBean2() {
            return bean2;
        }

    }

    static class Bean2 {
        private static final Logger LOGGER = LoggerFactory.getLogger(Bean2.class);

        public Bean2() {
            LOGGER.info("构造bean2");
        }
    }
}
