package org.springframework.boot;

import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.context.event.EventPublishingRunListener;
import org.springframework.boot.env.EnvironmentPostProcessorApplicationListener;
import org.springframework.boot.env.RandomValuePropertySourceEnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogs;
import org.springframework.core.env.PropertySource;

/**
 * spring.factories
 * 中配置了 EnvironmentPostProcessorApplicationListener 这个 listener
 * # Application Listeners
 * org.springframework.context.ApplicationListener=\
 * org.springframework.boot.ClearCachesApplicationListener,\
 * org.springframework.boot.builder.ParentContextCloserApplicationListener,\
 * org.springframework.boot.context.FileEncodingApplicationListener,\
 * org.springframework.boot.context.config.AnsiOutputApplicationListener,\
 * org.springframework.boot.context.config.DelegatingApplicationListener,\
 * org.springframework.boot.context.logging.LoggingApplicationListener,\
 * org.springframework.boot.env.EnvironmentPostProcessorApplicationListener
 *
 * # Environment Post Processors
 * org.springframework.boot.env.EnvironmentPostProcessor=\
 * org.springframework.boot.cloud.CloudFoundryVcapEnvironmentPostProcessor,\
 * org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor,\
 * org.springframework.boot.env.RandomValuePropertySourceEnvironmentPostProcessor,\
 * org.springframework.boot.env.SpringApplicationJsonEnvironmentPostProcessor,\
 * org.springframework.boot.env.SystemEnvironmentPropertySourceEnvironmentPostProcessor,\
 * org.springframework.boot.reactor.DebugAgentEnvironmentPostProcessor
 */
public class Step5 {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication();
        app.addListeners(new EnvironmentPostProcessorApplicationListener());

        EventPublishingRunListener publishingRunListener = new EventPublishingRunListener(app, args);
        ApplicationEnvironment env = new ApplicationEnvironment();
        System.out.println(">>>>>>>>>>>>>>>>>>> 增强前");
        for (PropertySource<?> propertySource : env.getPropertySources()) {
            System.out.println(propertySource);
        }
        publishingRunListener.environmentPrepared(new DefaultBootstrapContext(), env);
        System.out.println(">>>>>>>>>>>>>>>>>>> 增强后");
        // 可以看到我们刚发布这个事件以后，env已经被增强
        /**
         * PropertiesPropertySource {name='systemProperties'}
         * OriginAwareSystemEnvironmentPropertySource {name='systemEnvironment'}
         * RandomValuePropertySource {name='random'}
         * OriginTrackedMapPropertySource {name='Config resource 'class path resource [application.properties]' via location 'optional:classpath:/''}
         */
        for (PropertySource<?> propertySource : env.getPropertySources()) {
            System.out.println(propertySource);
        }
    }

    public static void test(String[] args) {
        SpringApplication app = new SpringApplication();
        ApplicationEnvironment env = new ApplicationEnvironment();

        System.out.println(">>>>>>>>>>>>>>>>>>> 增强前");
        for (PropertySource<?> propertySource : env.getPropertySources()) {
            System.out.println(propertySource);
        }
        // 加载 application.properties
        // OriginTrackedMapPropertySource
        ConfigDataEnvironmentPostProcessor postProcessor =
                new ConfigDataEnvironmentPostProcessor(new DeferredLogs(), new DefaultBootstrapContext());
        postProcessor.postProcessEnvironment(env, app);

        // 随机值环境配置加载
        // RandomValuePropertySource
        RandomValuePropertySourceEnvironmentPostProcessor randomProcessor
                = new RandomValuePropertySourceEnvironmentPostProcessor(new DeferredLogs().getLog(Step5.class));
        randomProcessor.postProcessEnvironment(env, app);

        System.out.println(">>>>>>>>>>>>>>>>>>> 增强后");
        for (PropertySource<?> propertySource : env.getPropertySources()) {
            System.out.println(propertySource);
        }

        System.out.println(env.getProperty("spring.application.name"));
        System.out.println(env.getProperty("random.int"));
        System.out.println(env.getProperty("random.int"));
        System.out.println(env.getProperty("random.int"));
    }
}
