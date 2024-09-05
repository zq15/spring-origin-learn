package com.example.boot.A23.sub;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.core.GenericTypeResolver;

public class TestGenericType {
    public static void main(String[] args) {
        // 小技巧
        // 1. jdk api
        Type type = StudentDao.class.getGenericSuperclass();
        System.out.println(type);

        if (type instanceof ParameterizedType parameterizedType) {
            System.out.println(parameterizedType.getActualTypeArguments()[0]);
        }

        // 2. spring api
        Class<?> t = GenericTypeResolver.resolveTypeArgument(StudentDao.class, BaseDao.class);
        System.out.println(t);

    }
}
