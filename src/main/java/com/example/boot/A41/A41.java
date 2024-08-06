package com.example.boot.A41;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

public class A41 {
    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        // 如果设置不允许覆盖，则报错：Exception in thread "main" org.springframework.beans.factory.support.BeanDefinitionOverrideException
//        context.setAllowBeanDefinitionOverriding(false);
        context.registerBean("config", Config.class);
        // 配置类后置处理器
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.refresh();

        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }

        // 默认是允许
        Bean1 bean = context.getBean(Bean1.class);
        System.out.println(bean.getName()); // 自己的
    }

    @Configuration
    @Import(MyImportSelector.class)
    static class Config {

        // 模拟自己的 bean 覆盖 spring.factories 中的
        @Bean
        public Bean1 bean1() {
            return new Bean1("自己的");
        }
    }

    // 如果修改为使用
    static class MyImportSelector implements DeferredImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
//            return new String[]{AutoConfiguration1.class.getName(), AutoConfiguration2.class.getName()};
            // 从 spring.factories 中读取
            /**
             * com.example.boot.A41.A41$MyImportSelector=\ 注意 内部类要用 $ 分割
             *   com.example.boot.A41.A41.AutoConfiguration1,\
             *   com.example.boot.A41.A41.AutoConfiguration2
             */
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
            // 打印 spring.factories 中配置的 ApplicationContextInitializer 实现类
            for (String s : SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class, null)) {
                System.out.println(s);
            }
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");

            List<String> strings = SpringFactoriesLoader.loadFactoryNames(MyImportSelector.class, null);
            return strings.toArray(new String[0]);
        }
    }

    @Configuration
    static class AutoConfiguration1 {
        @Bean
        @ConditionalOnMissingBean
        public Bean1 bean1() {
            return new Bean1("第三方bean");
        }
    }

    static class Bean1 {

        private String name;

        public Bean1(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Configuration
    static class AutoConfiguration2 {
        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }
    }

    static class Bean2 {

    }
}
