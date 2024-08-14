package org.springframework.aop.framework.autoproxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJMethodBeforeAdvice;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * @Before 前置通知会被转换为下面原始的 AspectJMethodBeforeAdvice 该对象包含如下信息
 * pointcut [AspectJExpressionPointcut: () execution(* foo())]
 * advice [org.springframework.aop.aspectj.AspectJMethodBeforeAdvice:
 * advice method [public void org.springframework.aop.framework.autoproxy.A17_2$Aspect.before1()]; aspect name '']
 * 类似的通知还有
 * 1.AspectJAroundAdvice 环绕通知
 * 2.AspectJAfterReturningAdvice 返回通知
 * 3.AspectJAfterThrowingAdvice 异常通知
 * 4.AspectJAfterAdvice 环绕通知
 */
public class A17_2 {
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
    }

    public static void main(String[] args) {
        AspectInstanceFactory factory = new SingletonAspectInstanceFactory(new Aspect());
        List<Advisor> advisors = new ArrayList<>();
        // 高级切面转低级切面
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
            }
        }
        for (Advisor advisor : advisors) {
            System.out.println(advisor);
        }
    }
}
