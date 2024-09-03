package org.springframework.aop.framework;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cglib.proxy.MethodProxy;

public class A18_1 {

    static class Target {
        public void foo()
        {
            System.out.println("Target.foo");
        }
    }

    static class Advice1 implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println("Advice1.before");
            Object result = invocation.proceed();
            System.out.println("Advice1.after");
            return result;
        }
    }

    static class Advice2 implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println("Advice2.before");
            Object result = invocation.proceed();
            System.out.println("Advice2.after");
            return result;
        }
    }

    static class MyInvocation implements MethodInvocation {

        private Object target;
        private Method method;
        private Object[] arguments;
        private List<MethodInterceptor> methodIninterceptors;

        private int count = 1;

        public MyInvocation(Object target, Method method, Object[] arguments, List<MethodInterceptor> methodIninterceptors) {
            this.target = target;
            this.method = method;
            this.arguments = arguments;
            this.methodIninterceptors = methodIninterceptors;
        }


        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public Object[] getArguments() {
            return arguments;
        }

        @Override
        public Object proceed() throws Throwable { // 调用每一个环绕通知
            if (count > methodIninterceptors.size()){
                // 调用目标，返回并结束递归
                return method.invoke(target, arguments);
            }
            // 逐一调用通知，count++
            MethodInterceptor interceptor = methodIninterceptors.get(count - 1);
            return interceptor.invoke(this);
        }

        @Override
        public Object getThis() {
            return target;
        }

        @Override
        public AccessibleObject getStaticPart() {
            return method;
        }
    }
}
