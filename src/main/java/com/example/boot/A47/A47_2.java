package com.example.boot.A47;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class A47_2 {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(A47_2.class);
        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
//        testArray(beanFactory);
//        testList(beanFactory);
        testApplicationContext(beanFactory);

        context.close();
    }

    private static void testQualifier(DefaultListableBeanFactory beanFactory) {

    }

    private static void testGeneric(DefaultListableBeanFactory beanFactory) {

    }

    private static void testApplicationContext(DefaultListableBeanFactory beanFactory) throws NoSuchFieldException, IllegalAccessException {
        DependencyDescriptor dd3 = new DependencyDescriptor(Target.class.getDeclaredField("applicationContext"), true);

        Field resolvableDependencies = DefaultListableBeanFactory.class.getDeclaredField("resolvableDependencies");
        resolvableDependencies.setAccessible(true);
        Map<Class<?>, Object> dependencies = (Map<Class<?>, Object>) resolvableDependencies.get(beanFactory);
        dependencies.forEach((k, v) -> System.out.println(k + ":" + v));
    }

    private static void testList(DefaultListableBeanFactory beanFactory) throws NoSuchFieldException {
        DependencyDescriptor dd1 = new DependencyDescriptor(Target.class.getDeclaredField("serviceList"), true);
        if (dd1.getDependencyType() == List.class){
            Class<?> componentType = dd1.getResolvableType().getGeneric().resolve(); // 取泛型中的方法
            System.out.println(componentType);
            // ....
        }
    }

    private static void testArray(DefaultListableBeanFactory beanFactory) throws NoSuchFieldException {
        DependencyDescriptor dd1 = new DependencyDescriptor(Target.class.getDeclaredField("serviceArray"), true);
        if (dd1.getDependencyType().isArray()){
            Class<?> componentType = dd1.getDependencyType().getComponentType();
            System.out.println(componentType);
            String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, componentType);
            List<Object> beans = new ArrayList<>();
            for (String name : names) {
                System.out.println(name);
                Object bean = dd1.resolveCandidate(name, componentType, beanFactory);
                beans.add(bean);
            }
            Object array = beanFactory.getTypeConverter().convertIfNecessary(beans, dd1.getDependencyType());
            System.out.println(beans);
        }
    }

    static class Target {
        @Autowired private Service[] serviceArray;
        @Autowired private List<Service> serviceList;
        @Autowired private ConfigurableApplicationContext applicationContext;
        @Autowired private Dao<Teacher> dao;
        @Autowired @Qualifier("service2") private Service service;
    }

    interface Service {

    }

    @Component("service1")
    static class Service1 implements Service {
    }

    @Component("service2")
    static class Service2 implements Service {
    }

    @Component("service3")
    static class Service3 implements Service {
    }

    interface Dao<T> {
    }

    static class Student {}

    static class Teacher {}

    @Component("dao1")
    static class Dao1 implements Dao<Student> {}

    @Component("dao2")
    static class Dao2 implements Dao<Teacher> {}
}
