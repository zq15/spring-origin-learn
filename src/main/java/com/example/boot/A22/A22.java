package com.example.boot.A22;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

public class A22 {
    public static void main(String[] args) throws NoSuchMethodException {
        // 1. 反射获取参数名
//        Method foo = Bean2.class.getMethod("foo", String.class, int.class);
//        for (Parameter parameter : foo.getParameters()) {
//            System.out.println(parameter.getName());
//        }

        // 2. 基于本地变量表来获取
//        LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
//        String[] parameterNames = discoverer.getParameterNames(foo);
//        System.out.println(Arrays.toString(parameterNames));

    }
}
