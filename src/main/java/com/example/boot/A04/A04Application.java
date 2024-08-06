package com.example.boot.A04;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.support.GenericApplicationContext;

/**
 * 常见后置处理器
 */
public class A04Application {
    public static void main(String[] args) {
        // 使用 GenericApplicationContext
        // 相对干净的实现，不含各种后置处理器
        GenericApplicationContext context = new GenericApplicationContext();

        // 用原始方法创建三个 bean
        context.registerBean("bean1", Bean1.class);
        context.registerBean("bean2", Bean2.class);
        context.registerBean("bean3", Bean3.class);
        context.registerBean("bean4", Bean4.class);

        // 处理 @Value 值注入
        context.getDefaultListableBeanFactory().setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        // 处理 @Autowired
        context.registerBean(AutowiredAnnotationBeanPostProcessor.class);

        context.registerBean(CommonAnnotationBeanPostProcessor.class); // @Resource @PostConstruct 生效 @PreDestroy 生效

        ConfigurationPropertiesBindingPostProcessor.register(context.getDefaultListableBeanFactory()); // @ConfigurationProperties 解析后置处理器

        // 初始化容器
        context.refresh(); // 执行 beanFactory 后处理器，添加 bean 后置处理器，初始化所有单例

        Bean4 bean4 = context.getBean(Bean4.class);
        System.out.println(bean4);

        // 销毁容器
        context.close();

    }
}
