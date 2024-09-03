package com.example.boot.A23;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;

// web 环境下的数据绑定
public class TestServletDataBinder {
    private static final Logger log = LoggerFactory.getLogger(TestServletDataBinder.class);

    public static void main(String[] args) throws Exception {
        // 执行数据绑定

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("birthday", "1999|03|04"); // 默认无法支持转换
        request.setParameter("address.name", "西安"); // 默认可以绑定成功

        User target = new User();

        /**
         * 1. 用工厂，无转换功能
         *         ServletRequestDataBinderFactory factory = new ServletRequestDataBinderFactory(null, null);
         *         WebDataBinder dataBinder = factory.createBinder(new ServletWebRequest(request), target, "user");
         * 2. 用 @InitBinder 对象 PropertyEditorRegistry PropertyEditor
         *         InvocableHandlerMethod method = new InvocableHandlerMethod(new MyController(), MyController.class.getMethod("aaa", WebDataBinder.class));
         *         ServletRequestDataBinderFactory factory = new ServletRequestDataBinderFactory(List.of(method), null);
         * 3. 用 ConversionService 转换
         *          FormattingConversionService service = new FormattingConversionService();
         *         service.addFormatter(new MyDateFormatter("ConversionService 方式扩展转换功能"));
         *         ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
         *         initializer.setConversionService(service);
         *         ServletRequestDataBinderFactory factory = new ServletRequestDataBinderFactory(null, initializer);
         * 4. 同时加了 @InitBinder ConversionService >>>>>>>>>>>> 进入了 用 @InitBinder 方式扩展的
         *  @InitBinder 优先级更高
         *         InvocableHandlerMethod method = new InvocableHandlerMethod(new MyController(), MyController.class.getMethod("aaa", WebDataBinder.class));
         *
         *         FormattingConversionService service = new FormattingConversionService();
         *         service.addFormatter(new MyDateFormatter("ConversionService 方式扩展转换功能"));
         *         ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
         *         initializer.setConversionService(service);
         *         ServletRequestDataBinderFactory factory = new ServletRequestDataBinderFactory(List.of(method), initializer);
         * 5. 使用默认 ConversionService 转换 >>>> 需要结合 @DateTimeFormat(pattern = "yyyy|MM|dd") 使用
         *         DefaultFormattingConversionService service = new DefaultFormattingConversionService(); // ApplicationConversionService boot 环境
         *         ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
         *         initializer.setConversionService(service);
         *         ServletRequestDataBinderFactory factory = new ServletRequestDataBinderFactory(null, initializer);
         */

//        DefaultFormattingConversionService service = new DefaultFormattingConversionService();
        ApplicationConversionService service = new ApplicationConversionService(); // SpringBoot 中的实现
        ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
        initializer.setConversionService(service);
        ServletRequestDataBinderFactory factory = new ServletRequestDataBinderFactory(null, initializer);

        WebDataBinder dataBinder = factory.createBinder(new ServletWebRequest(request), target, "user");
        dataBinder.bind(new ServletRequestParameterPropertyValues(request));
        System.out.println(target);
    }

    static class MyController {
        @InitBinder
        public void aaa(WebDataBinder dataBinder) {
            // 扩展 dataBinder 的转换器
            dataBinder.addCustomFormatter(new MyDateFormatter("用 @InitBinder 方式扩展的"));
        }
    }

    static class User {
        @DateTimeFormat(pattern = "yyyy|MM|dd")
        private Date birthday;
        private Address address;

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        @Override
        public String toString() {
            return "User{" +
                "birthday=" + birthday +
                ", address=" + address +
                '}';
        }
    }

    public static class Address {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Address{" +
                "name='" + name + '\'' +
                '}';
        }
    }

    static class MyBean {
        private int a;
        private String b;
        private Date c;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        public Date getC() {
            return c;
        }

        public void setC(Date c) {
            this.c = c;
        }

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
