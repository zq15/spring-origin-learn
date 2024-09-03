package org.springframework.aop.framework;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.aop.aspectj.AspectJAfterReturningAdvice;
import org.springframework.aop.aspectj.AspectJAroundAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJMethodBeforeAdvice;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * @Before 前置通知会被转换为下面原始的 AspectJMethodBeforeAdvice 该对象包含如下信息
 * pointcut [AspectJExpressionPointcut: () execution(* foo())]
 * advice [org.springframework.aop.aspectj.AspectJMethodBeforeAdvice:
 * advice method [public void org.springframework.aop.framework.autoproxy.A17_2$Aspect.before1()]; aspect name '']
 * 类似的通知还有
 * 1.AspectJAroundAdvice 环绕通知
 * 2.AspectJAfterReturningAdvice 返回通知
 * 3.AspectJAfterThrowingAdvice 异常通知 -> 环绕通知
 * 4.AspectJAfterAdvice 环绕通知
 */
public class A18 {
    static class Aspect {
        @Before("execution(* foo())")
        public void before1()
        {
            System.out.println("before1");
        }

        @Before("execution(* foo())")
        public void before2()
        {
            System.out.println("before2");
        }

        @AfterReturning("execution(* foo())")
        public void afterReturning()
        {
            System.out.println("afterReturning");
        }

        @Around("execution(* foo())")
        public void around()
        {
            System.out.println("around");
        }
    }

    static class Target {
        public void foo()
        {
            System.out.println("foo");
        }
    }

    public static void main(String[] args) throws Throwable {
        AspectInstanceFactory factory = new SingletonAspectInstanceFactory(new Aspect());
        List<Advisor> advisors = new ArrayList<>();
        // 1.高级切面转低级切面
        for (Method method : Aspect.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Before.class)) {
                // 解析切点
                String expression = method.getAnnotation(Before.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                // 通知类
                AspectJMethodBeforeAdvice advice = new AspectJMethodBeforeAdvice(method, pointcut, factory);
                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                advisors.add(advisor);
            } else if (method.isAnnotationPresent(AfterReturning.class)) {
                // 解析切点
                String expression = method.getAnnotation(AfterReturning.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                // 通知类
                AspectJAfterReturningAdvice advice = new AspectJAfterReturningAdvice(method, pointcut, factory);
                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                advisors.add(advisor);
            } else if (method.isAnnotationPresent(Around.class)) {
                // 解析切点
                String expression = method.getAnnotation(Around.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                // 通知类
                AspectJAroundAdvice advice = new AspectJAroundAdvice(method, pointcut, factory);
                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                advisors.add(advisor);
            }
        }
        for (Advisor advisor : advisors) {
            System.out.println(advisor);
        }
        //2.通知统一转换为环绕通知类型 MethodInterceptor
        /**
         * 因为无论 ProxyFactory 如何创建代理，最终调用 advisor 的都是一个 MethodInvocation 对象
         * 为了支持某些通知这样一层一层调用的形式，环绕通知肯定是最合适的
         * before 和 returning 需要转换
         */
        Target target = new Target();
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new Target());
        proxyFactory.addAdvice(ExposeInvocationInterceptor.INSTANCE); // 把 MethodInvocation 放入当前进程
        proxyFactory.addAdvisors(advisors);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>");
        List<Object> methodInterceptors = proxyFactory.getInterceptorsAndDynamicInterceptionAdvice(Target.class.getMethod("foo"), Target.class);
        for (Object o : methodInterceptors) {
            System.out.println(o); // MethodBeforeAdviceInterceptor AfterReturningAdviceInterceptor AspectJAroundAdvice 可以看到已经转换为类似于 AdviceInterceptor
        }
        // 适配器处理
        // MethodBeforeAdviceAdapter => MethodBeforeAdviceInterceptor -> AspectJAroundAdvice

        // 3. 创建并执行
        MethodInvocation methodInvocation = new ReflectiveMethodInvocation(
            null, target, Target.class.getMethod("foo"), new Object[0], Target.class, methodInterceptors
        );
        methodInvocation.proceed();
        // 调用链的一个递归过程

    }
}
