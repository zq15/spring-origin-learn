package org.springframework.boot;

import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

public class Step3 {
    public static void main(String[] args) throws Exception {
        ApplicationEnvironment env = new ApplicationEnvironment(); // 系统环境变量 yml properties
        // properties 来源的环境信息，没有添加
        env.getPropertySources().addLast(new ResourcePropertySource(new ClassPathResource("application.properties")));
        // 命令行来源的 properties
        env.getPropertySources().addFirst(new SimpleCommandLinePropertySource(args));
        for (PropertySource<?> ps : env.getPropertySources()) {
            System.out.println(ps);
        }
        System.out.println(env.getProperty("JAVA_HOME"));
        System.out.println(env.getProperty("spring.application.name"));
    }
}
