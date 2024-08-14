package org.springframework.aop.framework.autoproxy;

import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.Order;

/**
 * 1.高级切面和低级切面默认执行顺序 -> 低级 > 高级
 * advice3.before...
 * Aspect1.before...
 * Target1.foo()
 * Aspect1.after...
 * advice3.after...
 * 2. 手动调整后
 * 注意：低级切面只能调用 DefaultPointcutAdvisor.setOrder 方法手动修改顺序
 * 高级切面只能统一修改 @Aspect 标注的整个类的顺序
 *
 */
public class A17 {

    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("aspect1", Aspect1.class);
        context.registerBean("config", Config.class);
        context.registerBean(ConfigurationClassPostProcessor.class);

        context.registerBean(AnnotationAwareAspectJAutoProxyCreator.class);
        // 第一个重要方法 findEligibleAdvisors 找到有资格的 Advisors
        // 一部分是低级的，如 Advisor
        // 一部分是高级的，如 @Aspect 切面，需要解析成低级切面后再解析
        /**
         * \org.springframework.aop.support.DefaultPointcutAdvisor: pointcut [AspectJExpressionPointcut: () execution(* foo())]; advice [org.springframework.aop.framework.autoproxy.A17$Config$$Lambda$105/0x000001ee010feff8@20b2475a]
         * InstantiationModelAwarePointcutAdvisor: expression [execution(* foo())]; advice method [public void org.springframework.aop.framework.autoproxy.A17$Aspect1.before()]; perClauseKind=SINGLETON
         * InstantiationModelAwarePointcutAdvisor: expression [execution(* foo())]; advice method [public void org.springframework.aop.framework.autoproxy.A17$Aspect1.after()]; perClauseKind=SINGLETON
         */
        
        context.refresh();

//        String[] beanDefinitionNames = context.getBeanDefinitionNames();
//        for (String beanDefinitionName : beanDefinitionNames) {
//            System.out.println(beanDefinitionName);
//        }
        AnnotationAwareAspectJAutoProxyCreator creator = context.getBean(AnnotationAwareAspectJAutoProxyCreator.class);
        List<Advisor> advisors = creator.findEligibleAdvisors(Target1.class, "target1");
        for (Advisor advisor : advisors) {
            System.out.println(advisor);
        }

        // 第二个重要方法 wrapIfNecessary
        //   a. 内部调用 findEligibleAdvisors ，如果返回集合不为空，则表示需要创建代理
        Object o1 = creator.wrapIfNecessary(new Target1(), "target1", "target1");
        System.out.println(o1.getClass());
        Object o2 = creator.wrapIfNecessary(new Target2(), "target2", "target2");
        System.out.println(o2.getClass());

        ((Target1) o1).foo();
    }

    static class Target1 {
        public void foo()
        {
            System.out.println("Target1.foo()");
        }
    }

    static class Target2 {
        public void bar()
        {
            System.out.println("Target2.bar()");
        }
    }

    @Aspect
    @Order(1)
    static class Aspect1 {
        @Before("execution(* foo())")
        public void before() {
            System.out.println("Aspect1.before...");
        }

        @After("execution(* foo())")
        public void after() {
            System.out.println("Aspect1.after...");
        }
    }

    @Configuration
    static class Config {
        // 切点
        @Bean
        public Advisor advisor3(MethodInterceptor advice3) {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("execution(* foo())");
            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice3);
            advisor.setOrder(2);
            return advisor;
        }

        // 通知
        @Bean
        public MethodInterceptor advice3() {
            return invocation -> {
                System.out.println("advice3.before...");
                Object retVal = invocation.proceed();
                System.out.println("advice3.after...");
                return retVal;
            };
        }
    }
}
