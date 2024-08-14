package com.example.boot.A16;

import java.lang.reflect.Method;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.transaction.annotation.Transactional;

/**
 * 底层切点是如何匹配的
 * 比较关键的是实现了 MethodMatcher 接口
 */
public class A16 {
    public static void main(String[] args) throws NoSuchMethodException {
        // 1. 备好切面
        AspectJExpressionPointcut pointcut1 = new AspectJExpressionPointcut();
        pointcut1.setExpression("execution(* foo())");

        // 1. 这里我们调用 pointcut的 match 函数来判断，目标类的方法是否符合切点表达式
        System.out.println(pointcut1.matches(T1.class.getMethod("foo"), T1.class));
        System.out.println(pointcut1.matches(T1.class.getMethod("bar"), T1.class));

        // 2. 判断注解
        AspectJExpressionPointcut pointcut2 = new AspectJExpressionPointcut();
        pointcut2.setExpression("@annotation(org.springframework.transaction.annotation.Transactional)");

        System.out.println(pointcut2.matches(T1.class.getMethod("foo"), T1.class));
        System.out.println(pointcut2.matches(T1.class.getMethod("bar"), T1.class));

        // 3. 处理判断注解存在的不同位置处理
        /**
         * 1. 方法上
         * 2. 类上
         * 3. 接口上
         */
        StaticMethodMatcherPointcut pointcut3 = new StaticMethodMatcherPointcut() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                // 方法上
                MergedAnnotations annotations = MergedAnnotations.from(method);
                if (annotations.isPresent(Transactional.class)) {
                    return true;
                }
                // 类上 和 接口上
                annotations = MergedAnnotations.from(targetClass, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY); // 找继承树上的接口
                if (annotations.isPresent(Transactional.class)) {
                    return true;
                }
                return false;
            }
        };

        System.out.println(pointcut3.matches(T1.class.getMethod("foo"), T1.class));
        System.out.println(pointcut3.matches(T1.class.getMethod("bar"), T1.class));
        System.out.println(pointcut3.matches(T2.class.getMethod("foo"), T1.class));

    }

    static class T1 {
        @Transactional
        public void foo() {
        }

        public void bar() {
        }
    }

    @Transactional
    static class T2 {
        public void foo() {
        }
    }

    @Transactional
    interface I3 {
        void foo();
    }

    static class T3 implements I3 {
        public void foo() {
        }
    }

}