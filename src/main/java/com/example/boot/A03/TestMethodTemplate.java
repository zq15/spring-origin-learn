package com.example.boot.A03;

import java.util.ArrayList;
import java.util.List;

/**
 * 固定的步骤无需修改，变化的步骤修改成模板方法，抽象成接口调用
 * getBean 方法无需修改
 */
public class TestMethodTemplate {
    public static void main(String[] args) {
        MyBeanFactory beanFactory = new MyBeanFactory();
        beanFactory.addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public void inject(Object bean) {
                System.out.println("解析 @Autowired");
            }
        });
        beanFactory.addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public void inject(Object bean) {
                System.out.println("解析 @Resource");

            }
        });
        beanFactory.getBean();
    }

    // 模板方法
    static class MyBeanFactory {
        public Object getBean() {
            Object bean = new Object();
            System.out.println("构造: " + bean);
            System.out.println("依赖注入: " + bean);
            for (BeanPostProcessor processor : processors) {
                processor.inject(bean);
            }
            System.out.println("初始化: " + bean);
            return bean;
        }

        private List<BeanPostProcessor> processors = new ArrayList<>();

        public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor){
            processors.add(beanPostProcessor);
        }
    }

    static interface BeanPostProcessor {
        void inject(Object bean); // 对依赖注入阶段的功能进行扩展
    }
}
