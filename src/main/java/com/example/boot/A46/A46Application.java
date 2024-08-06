package com.example.boot.A46;

import com.example.boot.A43.Bean1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Configuration
@SuppressWarnings("all")
public class A46Application {
    public static void main(String[] args) throws NoSuchFieldException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(A46Application.class);

        ContextAnnotationAutowireCandidateResolver resolver = new ContextAnnotationAutowireCandidateResolver();
        resolver.setBeanFactory(context);

//        test1(Bean1.class.getDeclaredField("home"), resolver, context);
//        test2(Bean1.class.getDeclaredField("age"), resolver, context);
//        test3(Bean2.class.getDeclaredField("bean3"), resolver, context);
        test3(Bean4.class.getDeclaredField("value"), resolver, context);

    }

    private static void test1(Field field, ContextAnnotationAutowireCandidateResolver resolver, AnnotationConfigApplicationContext context) {
        // 获取 @Value 的内容
        DependencyDescriptor dd1 = new DependencyDescriptor(field, false);
        String value = resolver.getSuggestedValue(dd1).toString();
        System.out.println(value);

        // 解析 ${}
        value = context.getEnvironment().resolvePlaceholders(value);
        System.out.println(value);
    }

    private static void test2(Field field, ContextAnnotationAutowireCandidateResolver resolver, AnnotationConfigApplicationContext context) {
        DependencyDescriptor dd1 = new DependencyDescriptor(field, false);
        String value = resolver.getSuggestedValue(dd1).toString();
        System.out.println(value);

        // 解析 ${}
        value = context.getEnvironment().resolvePlaceholders(value);
        System.out.println(value);
        System.out.println(value.getClass());
        Object age = context.getBeanFactory().getTypeConverter().convertIfNecessary(value, dd1.getDependencyType());
        System.out.println(age.getClass());
    }

    private static void test3(Field field, ContextAnnotationAutowireCandidateResolver resolver, AnnotationConfigApplicationContext context) {
        // 获取 @Value 的内容
        DependencyDescriptor dd1 = new DependencyDescriptor(field, false);
        String value = resolver.getSuggestedValue(dd1).toString();
        System.out.println(value);
        // 解析 ${}
        value = context.getEnvironment().resolvePlaceholders(value);
        // 处理 #{} SpEL 表达式
        Object originResult = context.getBeanFactory().getBeanExpressionResolver().evaluate(value, new BeanExpressionContext(context.getBeanFactory(), null));
        Object result = context.getBeanFactory().getTypeConverter().convertIfNecessary(originResult, dd1.getDependencyType());
        System.out.println(result);
    }

    public class Bean1 {
        @Value("${JAVA_HOME}")
        private String home;
        @Value("18")
        private int age;
    }

    public class Bean2 {
        @Value("#{@bean3}") // SpEL 表达式
        private Bean3 bean3;
    }

    @Component("bean3")
    public class Bean3 {

    }

    static class Bean4 {
        @Value("#{'hello, ' + '${JAVA_HOME}'}")
        private String value;
    }
}
