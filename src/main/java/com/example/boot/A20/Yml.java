package com.example.boot.A20;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 自定义 @Yml 注解，用于标记返回 YAML 格式的数据
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Yml {
    // 可以添加自定义属性，这里保持简单，没有添加任何属性
}
