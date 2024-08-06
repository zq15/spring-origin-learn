package com.example.boot.A41;

import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;

public class TestAnnoAuto {
    public static void main(String[] args) {
        /**
         * 1. 下面这些是在 config 中注册进来的配置类
         * org.springframework.boot.autoconfigure.aop.AopAutoConfiguration$AspectJAutoProxyingConfiguration$CglibAutoProxyConfiguration
         * org.springframework.aop.config.internalAutoProxyCreator
         * org.springframework.boot.autoconfigure.aop.AopAutoConfiguration$AspectJAutoProxyingConfiguration
         * org.springframework.boot.autoconfigure.aop.AopAutoConfiguration
         */
        GenericApplicationContext context = new GenericApplicationContext();
        /**
         * 2. 源码中 AopAutoConfiguration 设定只有在 spring.aop.auto 为 false 的时候才不生效，matchIfMissing 标识不配置也生效
         *
         * @AutoConfiguration
         * @ConditionalOnProperty(prefix = "spring.aop", name = "auto", havingValue = "true", matchIfMissing = true)
         * public class AopAutoConfiguration {
         */
        StandardEnvironment env = new StandardEnvironment();
        env.getPropertySources().addLast(new SimpleCommandLinePropertySource("--spring.aop.auto=true"));

        /**
         * 3.  Advice 类是否存在的判断分支 存在注册 AspectJAutoProxyingConfiguration，不存在注册 ClassProxyingConfiguration
         * @ConditionalOnClass(Advice.class)
         * static class AspectJAutoProxyingConfiguration {
         *
         * @ConditionalOnMissingClass("org.aspectj.weaver.Advice")
         * static class ClassProxyingConfiguration {
         *
         */
        /**
         * 4. 判断分支
         * 4-1
         * CglibAutoProxyConfiguration  spring.aop.proxy-target-class=false
         * JdkDynamicAutoProxyConfiguration spring.aop.proxy-target-class=true
         * 4-2
         * 但无论如何都会使用 @EnableAspectJAutoProxy(proxyTargetClass = false)
         * 这一步最终其实是为了注册 internalAutoProxyCreator
         * todo spring aop源码解析
         *
         * proxyTargetClass 都是 false，参考下面注册的代码，我们可以看到，会走 cglib 代理
         * 最下面有测试的代码
         * if (enableAspectJAutoProxy != null) {
         *             if (enableAspectJAutoProxy.getBoolean("proxyTargetClass")) {
         *                 AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry); //
         *             }
         *
         *             if (enableAspectJAutoProxy.getBoolean("exposeProxy")) {
         *                 AopConfigUtils.forceAutoProxyCreatorToExposeProxy(registry);
         *             }
         *         }
         *
         *  public static void forceAutoProxyCreatorToUseClassProxying(BeanDefinitionRegistry registry) {
         *         if (registry.containsBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator")) {
         *             BeanDefinition definition = registry.getBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator");
         *             definition.getPropertyValues().add("proxyTargetClass", Boolean.TRUE);
         *         }
         *     }
         * public static void forceAutoProxyCreatorToExposeProxy(BeanDefinitionRegistry registry) {
         *         if (registry.containsBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator")) {
         *             BeanDefinition definition = registry.getBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator");
         *             definition.getPropertyValues().add("exposeProxy", Boolean.TRUE);
         *         }
         *
         *     }
         */
        env.getPropertySources().addLast(new SimpleCommandLinePropertySource("--spring.aop.proxy-target-class=true"));

        context.setEnvironment(env);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context.getDefaultListableBeanFactory());
        context.registerBean(Config.class);
        context.refresh();
        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        //
        AnnotationAwareAspectJAutoProxyCreator creator =
                context.getBean("org.springframework.aop.config.internalAutoProxyCreator", AnnotationAwareAspectJAutoProxyCreator.class);
        System.out.println(creator.isProxyTargetClass()); // true
    }

    @Configuration
    @Import(MyImportSelector.class)
    static class Config {

    }

    static class MyImportSelector implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{AopAutoConfiguration.class.getName()};
        }
    }

}
