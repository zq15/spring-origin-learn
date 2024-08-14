package com.example.boot.A14;

import java.lang.reflect.Method;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class A14 {
    public static void main(String[] args) {
        Proxy proxy = new Proxy();
        Target target = new Target();
        proxy.setMethodInterceptor(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy proxy) throws Throwable {
                System.out.println("before");
                // fastClass 避免反射调用
//                return method.invoke(target, objects); // 反射调用
//                return proxy.invoke(target, objects); // 内部无反射，结合目标用
                return proxy.invokeSuper(o, objects); // 内部无反射，结合代理用
            }
        });

        proxy.save();
        proxy.save(1);
        proxy.save(1L);
    }
}
