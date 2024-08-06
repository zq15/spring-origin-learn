package com.example.boot.A40;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11Nio2Protocol;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestTomcat {
    public static void main(String[] args) throws IOException, LifecycleException {
        // 1.创建 Tomcat 对象
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("tomcat");

        // 2.创建项目文件夹
        File docBase = Files.createTempDirectory("boot.").toFile();
        docBase.deleteOnExit();

        // 3.创建tomcat项目，在 tomcat 中称为 context
        Context context = tomcat.addContext("", docBase.getAbsolutePath());

        WebApplicationContext webApplicationContext = getWebApplicationContext();

        // 4.编程添加 Servlet
        context.addServletContainerInitializer(new ServletContainerInitializer() {
            @Override
            public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
                servletContext.addServlet("hello", new HelloServlet()).addMapping("/hello");

                // 1.不通用的配置方式
//                DispatcherServlet dispatcherServlet = webApplicationContext.getBean(DispatcherServlet.class);
//                servletContext.addServlet("dispatcherServlet", dispatcherServlet).addMapping("/");
                // 2.我们通过 DispatcherServletRegistrationBean 来注册 DispatcherServlet
                webApplicationContext.getBeansOfType(ServletRegistrationBean.class).values().forEach(registrationBean -> {
                    try {
                        registrationBean.onStartup(servletContext);
                    } catch (ServletException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }, Collections.emptySet());

        // 5. 启动 Tomcat
        tomcat.start();

        // 6. 创建连接器，设置监听接口
        Connector connector = new Connector(new Http11Nio2Protocol());
        connector.setPort(8080);
        tomcat.setConnector(connector);

    }

    public static WebApplicationContext getWebApplicationContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(Config.class);
        context.refresh();
        return context;
    }

    @Configuration
    static class Config {

        @Bean
        public DispatcherServletRegistrationBean registrationBean(DispatcherServlet dispatcherServlet) {
            return new DispatcherServletRegistrationBean(dispatcherServlet, "/");
        }

        @Bean
        public DispatcherServlet dispatcherServlet(WebApplicationContext webApplicationContext) {
            return new DispatcherServlet(webApplicationContext);
        }

        @Bean
        public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
            RequestMappingHandlerAdapter handlerAdapter = new RequestMappingHandlerAdapter();
            handlerAdapter.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter()));
            return handlerAdapter;
        }

        @RestController
        static class MyController{
            @GetMapping("hello2")
            public Map<String, Object> hello() {
                return Map.of("hello2", "hello2, spring");
            }
        }
    }
}
