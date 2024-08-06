package com.example.boot.A03;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 构造 -> 依赖注入 -> 初始化 -> 初始化 -> 销毁
 * 2024-08-02 15:45:35.417  INFO 26632 --- [           main] c.example.boot.A03.MyBeanPostProcessor   : <<<<<<<<<<<<<<< 实例化之前执行，这里返回的对象会替换原来的 bean
 * 2024-08-02 15:45:35.418  INFO 26632 --- [           main] com.example.boot.A03.LifeCycleBean       : 构造
 * 2024-08-02 15:45:35.420  INFO 26632 --- [           main] c.example.boot.A03.MyBeanPostProcessor   : <<<<<<<<<<<<<<< 实例化之后执行，返回 false 会跳过依赖注入阶段
 * 2024-08-02 15:45:35.420  INFO 26632 --- [           main] c.example.boot.A03.MyBeanPostProcessor   : <<<<<<<<<<<<<<< 依赖注入阶段执行，@Autowired，@Value，@Resource
 * 2024-08-02 15:45:35.421  INFO 26632 --- [           main] com.example.boot.A03.LifeCycleBean       : 依赖注入: D:\tool\zulu-8
 * 2024-08-02 15:45:35.422  INFO 26632 --- [           main] c.example.boot.A03.MyBeanPostProcessor   : >>>>>>>>>>>>>>>> 初始化之前执行，这里返回的对象会替换掉原来的 bean，如 @PostConstruct, @ConfigurationProperties
 * 2024-08-02 15:45:35.422  INFO 26632 --- [           main] com.example.boot.A03.LifeCycleBean       : 初始化
 * 2024-08-02 15:45:35.422  INFO 26632 --- [           main] c.example.boot.A03.MyBeanPostProcessor   : >>>>>>>>>>>>>>  初始化之后执行，这里返回的对象会替换原本的 bean，如代理增强
 * 2024-08-02 15:45:35.598  INFO 26632 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
 * 2024-08-02 15:45:35.605  INFO 26632 --- [           main] com.example.boot.A03.A03Application      : Started A03Application in 1.015 seconds (JVM running for 1.389)
 * 2024-08-02 15:45:35.635  INFO 26632 --- [           main] o.apache.catalina.core.StandardService   : Stopping service [Tomcat]
 * 2024-08-02 15:45:35.643  INFO 26632 --- [           main] c.example.boot.A03.MyBeanPostProcessor   : <<<<<<<< 销毁之前执行，@PreDestroy
 * 2024-08-02 15:45:35.643  INFO 26632 --- [           main] com.example.boot.A03.LifeCycleBean       : 销毁
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class A03Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(A03Application.class, args);
        context.close();
    }
}
