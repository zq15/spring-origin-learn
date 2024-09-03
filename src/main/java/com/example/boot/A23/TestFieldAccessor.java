package com.example.boot.A23;

import java.util.Date;
import org.springframework.beans.DirectFieldAccessor;

public class TestFieldAccessor {
    public static void main(String[] args) {
        // 利用反射原理，为 bean 的属性赋值
        MyBean target = new MyBean();
        DirectFieldAccessor accessor = new DirectFieldAccessor(target);
        accessor.setPropertyValue("a", "10");
        accessor.setPropertyValue("b", "hello");
        accessor.setPropertyValue("c", "1999/03/04");
        System.out.println(target);
    }

    static class MyBean {
        private int a;
        private String b;
        private Date c;

        @Override
        public String toString() {
            return "MyBean{" +
                "a=" + a +
                ", b='" + b + '\'' +
                ", c=" + c +
                '}';
        }
    }
}
