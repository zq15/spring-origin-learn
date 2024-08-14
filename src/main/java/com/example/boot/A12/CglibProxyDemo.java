package com.example.boot.A12;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

public class CglibProxyDemo {

    // 类和方法都不能是 final
    static class Target {
        public void foo() {
            System.out.println("foo");
        }
    }

    // 代理类是子类型，目标是父类型
    public static void main(String[] args) {
        Target target = new Target();
        Target proxy = (Target) Enhancer.create(Target.class, (MethodInterceptor) (p, method, param, proxyMethod) -> {
            System.out.println("before...");
//            Object result = method.invoke(target, param); // 用方法反射来调用目标
//            Object result = proxyMethod.invoke(target, param); // 内部不使用反射，需要目标 -> spring
            Object result = proxyMethod.invokeSuper(p, param); // 内部不使用反射，需要代理
            System.out.println("after...");
            return result;
        });

        proxy.foo();
    }
}
