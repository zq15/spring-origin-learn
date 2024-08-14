package com.example.boot.A12;

import java.io.IOException;
import java.lang.reflect.Proxy;

public class JdkProxyDemo {
    interface Foo {
        void foo();
    }

    // 可以是 final ，目标和原对象之间是兄弟关系
    static final class Target implements Foo {
        public void foo() {
            System.out.println("target foo");
        }
    }

    public static void main(String[] args) throws IOException {
        // 目标对象
        Target target = new Target();

        // 生成的代理类的字节码也需要加载后才能运行
        ClassLoader loader = JdkProxyDemo.class.getClassLoader(); // 用来加载运行期间动态生成的字节码
        Foo proxy = (Foo) Proxy.newProxyInstance(loader, new Class[] {Foo.class}, (p, method, param) -> {
            System.out.println("before...");
            Object result = method.invoke(target, param);
            System.out.println("after...");
            return result;
        });

        System.out.println(proxy.getClass());

        proxy.foo();

        System.in.read();
    }
}
