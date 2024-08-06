package com.example.boot.A43;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * 在spring发展阶段中重要，但目前已经很鸡肋的接口 FactoryBean 的使用要点
 * 1. 作用是用于创建较为复杂的产品如 SqlSessionFactory，但 @Bean 已具备等价功能
 * 2. 使用税较为古怪，一不留神就会用错
 *      a. 被 Factory 创建的 Bean
 *          - 会认为创建，依赖注入，Aware 接口回调，前初始化这些都是 FactoryBean 的职责，这些流程都不会走
 *          - 唯有后初始化的流程会走，也就是产品可以被代理增强
 *          - 单例的产品不会存储于 BeanFactory 的 singletonObjects 成员中，而是另一个 factoryBeanObjectCache 成员中
 *      b. 按名字取获取时，拿到的是产品对象，名字前加 & 获取的是工厂对象
 * 工厂管理的bean在初始化后可以被增强，其他阶段不会
 */
@ComponentScan
public class A43Application {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(A43Application.class);

        Bean1 bean1 = (Bean1) context.getBean("bean1");
        Bean1 bean2 = (Bean1) context.getBean("bean1");
        Bean1 bean3 = (Bean1) context.getBean("bean1");

        System.out.println(bean1);
        System.out.println(bean2);
        System.out.println(bean3);

        System.out.println(context.getBean(Bean1FactoryBean.class));
        System.out.println(context.getBean("&bean1"));

        context.close();
    }
}
