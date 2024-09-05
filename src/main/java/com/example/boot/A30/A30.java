package com.example.boot.A30;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

public class A30 {
    public static void main(String[] args) throws NoSuchMethodException {
        ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
        resolver.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter())); // 4.添加 json 转换器, 解析 Map 为 json 返回
        resolver.afterPropertiesSet(); // 添加默认的参数解析器和返回值处理器

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 1.测试 json, 对应异常处理方法使用 @ResponseBody
//        HandlerMethod handlerMethod = new HandlerMethod(new Controller1(), "foo"); // 1.找类上面的 @ExceptionHandler
//        Exception e = new ArithmeticException("被零除");
//        resolver.resolveException(request, response, handlerMethod, e);
//        System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
        // 2.解析 mav 对应异常处理方法返回 mav
//        HandlerMethod handlerMethod = new HandlerMethod(new Controller2(), Controller2.class.getMethod("foo"));
//        Exception e = new ArithmeticException("被零除");
//        ModelAndView mav = resolver.resolveException(request, response, handlerMethod, e);
//        System.out.println(mav.getModel());
//        System.out.println(mav.getViewName());
        // 3.测试异常嵌套
        /**
         * 对应源码，不断找 cause，直到为 null
         * while (exToExpose != null) {
         * 				exceptions.add(exToExpose);
         * 				Throwable cause = exToExpose.getCause();
         * 				exToExpose = (cause != exToExpose ? cause : null);
         * 			            }
         */
//        HandlerMethod handlerMethod = new HandlerMethod(new Controller3(), Controller3.class.getMethod("foo"));
//        Exception e = new Exception("e1", new RuntimeException("e2", new IOException("e3"))); // 嵌套的异常
//        resolver.resolveException(request, response, handlerMethod, e);
//        System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8)); // e3 找到最内层异常
        // 4.测试异常处理方法参数解析
        HandlerMethod handlerMethod = new HandlerMethod(new Controller4(), Controller4.class.getMethod("foo"));
        Exception e = new Exception("e1");
        resolver.resolveException(request, response, handlerMethod, e);
        System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
    }

    static class Controller1 {
        public void foo() {

        }
        @ExceptionHandler
        @ResponseBody         // 2.返回值处理
        public Map<String, Object> handle(ArithmeticException e) { // 3.匹配有没有这个 异常
            return Map.of("error", e.getMessage());
        }
    }

    static class Controller2 {
        public void foo() {

        }
        @ExceptionHandler
        public ModelAndView handle(ArithmeticException e) {
            return new ModelAndView("test2", Map.of("error", e.getMessage()));
        }
    }

    static class Controller3 {
        public void foo() {

        }
        @ExceptionHandler
        @ResponseBody
        public Map<String, Object> handle(IOException e3) {
            return Map.of("error", e3.getMessage());
        }
    }

    static class Controller4 {
        public void foo() {}
        @ExceptionHandler
        @ResponseBody
        public Map<String, Object> handler(Exception e, HttpServletRequest request) { // 可以拿到 request 中信息, 对应 ServletRequestMethodArgumentResolver 解析器
            System.out.println(request);
            return Map.of("error", e.getMessage());
        }
    }
}
