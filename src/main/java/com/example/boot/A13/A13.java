package com.example.boot.A13;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class A13 {
    interface Foo {
        void foo() throws NoSuchMethodException;
        void bar() throws NoSuchMethodException;
    }

    static class Target implements Foo {
        public void foo()
        {
            System.out.println("Target.foo()");
        }

        @Override
        public void bar() {
            System.out.println("Target.bar()");
        }
    }

    interface InvocationHandler {
        void invoke(Method method, Object[] args) throws Throwable, IllegalAccessException;
    }

    public static void main(String[] args) throws Throwable {
        Foo proxy = new $Proxy0(new InvocationHandler() {
            @Override
            public void invoke(Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
                // 1. 功能增强
                System.out.println("before....");
                // 2. 调用目标
                method.invoke(new Target(), args);
            }
        });
        proxy.foo();
        proxy.bar();//??
    }
}
