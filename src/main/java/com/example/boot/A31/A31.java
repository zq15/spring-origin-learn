package com.example.boot.A31;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

public class A31 {
    public static void main(String[] args) throws NoSuchMethodException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

//        ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
//        resolver.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter()));
//        resolver.afterPropertiesSet();

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(WebConfig.class);
        ExceptionHandlerExceptionResolver resolver = context.getBean(ExceptionHandlerExceptionResolver.class);
        // InitializingBean 实现了，所以无需 afterPropertiesSet()，只要被 容器管理
        // 启动阶段，会去 找 @ControllerAdvice 的 @ExceptionHandler
        /**
         * 对应源码
         * List<ControllerAdviceBean> adviceBeans = ControllerAdviceBean.findAnnotatedBeans(getApplicationContext()); // 找到所有 @ControllerAdvice 的 Bean
         * 		for (ControllerAdviceBean adviceBean : adviceBeans) {
         * 			Class<?> beanType = adviceBean.getBeanType();
         * 			if (beanType == null) {
         * 				throw new IllegalStateException("Unresolvable type for ControllerAdviceBean: " + adviceBean);
         * 			            }
         * 			ExceptionHandlerMethodResolver resolver = new ExceptionHandlerMethodResolver(beanType);
         * 			if (resolver.hasExceptionMappings()) {
         * 				this.exceptionHandlerAdviceCache.put(adviceBean, resolver);
         *            }
         * 			if (ResponseBodyAdvice.class.isAssignableFrom(beanType)) {
         * 				this.responseBodyAdvice.add(adviceBean);
         *            }
         *            }
         */

        HandlerMethod handlerMethod = new HandlerMethod(new Controller5(), Controller5.class.getMethod("foo"));
        Exception e = new Exception("e1");
        resolver.resolveException(request, response, handlerMethod, e);
        System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
    }

    static class Controller5 {
        public void foo() {

        }
    }
}
