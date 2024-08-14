package com.example.boot.A13;

import java.lang.reflect.Method;

public class $Proxy0 implements A13.Foo {

    private A13.InvocationHandler invocationHandler;

    public $Proxy0(A13.InvocationHandler invocationHandler) {
        this.invocationHandler = invocationHandler;
    }

    @Override
    public void foo() {
        try {
            Method foo = A13.Foo.class.getDeclaredMethod("foo");
            invocationHandler.invoke(foo, new Object[0]);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void bar() {
        try {
            Method bar = A13.Foo.class.getDeclaredMethod("bar");
            invocationHandler.invoke(bar, new Object[0]);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
