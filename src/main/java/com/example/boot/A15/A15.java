package com.example.boot.A15;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * spring 对应代理实现的选择
 * 一共两种代理
 * 1. jdk 动态代理
 * 2. cglib 动态代理
 * spring 底层的切点实现
 * spring 底层的通知实现
 */
public class A15 {
    public static void main(String[] args) {
        // 1. 备好切面
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* foo())");

        // 2. 备好通知
        MethodInterceptor methodInterceptor = invocation -> {
            System.out.println("before");
            Object result = invocation.proceed();
            System.out.println("after");
            return result;
        };

        // 3. 备好切面
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, methodInterceptor);

        // 4. 创建代理
        /**
         * a. proxyTargetClass = true cglib
         * b. proxyTargetClass = false 实现了接口 jdk
         * c. proxyTargetClass = false 没有实现接口 cglib
         */
        Target1 target = new Target1();
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        proxyFactory.addAdvisor(advisor);
        proxyFactory.setInterfaces(target.getClass().getInterfaces()); // 默认 spring 不知道你实现了接口需要手动标注 class com.example.boot.A15.$Proxy0
        proxyFactory.setProxyTargetClass(true);
        I1 proxy = (I1) proxyFactory.getProxy();
        System.out.println(proxy.getClass()); // class com.example.boot.A15.A15$Target1$$EnhancerBySpringCGLIB$$f2027dfe
        proxy.foo();
        proxy.bar();
    }

    interface I1 {
        void foo();
        void bar();
    }

    static class Target1 implements I1 {
        public void foo() {
            System.out.println("foo");
        }
        public void bar() {
            System.out.println("bar");
        }
    }

}
