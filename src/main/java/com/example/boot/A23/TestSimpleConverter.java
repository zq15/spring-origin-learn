package com.example.boot.A23;

import java.util.Date;
import org.springframework.beans.SimpleTypeConverter;

public class TestSimpleConverter {
    public static void main(String[] args) {
        // 仅有类型转换的功能
        SimpleTypeConverter converter = new SimpleTypeConverter();
        System.out.println(converter.convertIfNecessary("13", int.class));
        System.out.println(converter.convertIfNecessary("1999/03/04", Date.class));
    }
}
