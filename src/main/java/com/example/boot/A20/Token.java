package com.example.boot.A20;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 自定义 @Token 注解，用于接收令牌参数
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Token {
    // 可以添加自定义属性，这里保持简单，没有添加任何属性
}
