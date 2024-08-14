package org.springframework.aop.framework.autoproxy;

import javax.annotation.PostConstruct;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

/**
 * (1) 创建代理的时机
 * 构造-> * ->依赖注入 -> 初始化 *
 * 1.bean1 单向依赖 bean2
 * bean1 初始化之后才创建代理
 * Bean1()
 * Bean1 init()
 * 11:00:04.840 [main] TRACE o.s.a.a.a.AnnotationAwareAspectJAutoProxyCreator - Creating implicit proxy for bean 'bean1' with 0 common interceptors and 2 specific interceptors
 * Bean2()
 * Bean2 setBean1class org.springframework.aop.framework.autoproxy.A17_1$Bean1$$EnhancerBySpringCGLIB$$81bf1a6b
 * Bean2 init()
 * 2.bean1 和 bean2 循环依赖
 * bean1 构造和依赖注入之间创建，存入二级缓存
 * Bean1()
 * Bean2()
 * 11:01:49.577 [main] TRACE o.s.a.a.a.AnnotationAwareAspectJAutoProxyCreator - Creating implicit proxy for bean 'bean1' with 0 common interceptors and 2 specific interceptors
 * Bean2 setBean1class org.springframework.aop.framework.autoproxy.A17_1$Bean1$$EnhancerBySpringCGLIB$$d2365e0b
 * Bean2 init()
 * Bean1 setBean2class org.springframework.aop.framework.autoproxy.A17_1$Bean2
 * Bean1 init()
 * (2) 依赖注入和初始化不应该被增强
 */
public class A17_1 {

    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("config", Config.class);
        context.registerBean(ConfigurationClassPostProcessor.class);

        context.registerBean(AutowiredAnnotationBeanPostProcessor.class);
        context.registerBean(CommonAnnotationBeanPostProcessor.class);
        context.registerBean(AnnotationAwareAspectJAutoProxyCreator.class);

        context.refresh();

    }

    static class Bean1 {
        public void foo(){}
        public Bean1() {
            System.out.println("Bean1()");
        }
        @Autowired public void setBean2(Bean2 bean2) {
            System.out.println("Bean1 setBean2" + bean2.getClass());
        }
        @PostConstruct public void init() {
            System.out.println("Bean1 init()");
        }
    }
    static class Bean2 {
        public Bean2() {
            System.out.println("Bean2()");
        }
        @Autowired public void setBean1(Bean1 bean1) {
            System.out.println("Bean2 setBean1" + bean1.getClass());
        }
        @PostConstruct public void init() {
            System.out.println("Bean2 init()");
        }
    }

    @Configuration
    static class Config {
        // 切点
        @Bean
        public Advisor advisor3(MethodInterceptor advice3) {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("execution(* foo())");
            return new DefaultPointcutAdvisor(pointcut, advice3);
        }

        // 通知
        @Bean
        public MethodInterceptor advice3() {
            return invocation -> {
                System.out.println("advice3.before...");
                Object retVal = invocation.proceed();
                System.out.println("advice3.after...");
                return retVal;
            };
        }

        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }
    }
}
