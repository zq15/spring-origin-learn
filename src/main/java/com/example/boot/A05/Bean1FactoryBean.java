package com.example.boot.A05;

import org.springframework.beans.factory.FactoryBean;

public class Bean1FactoryBean implements FactoryBean<Bean1> {

    @Override
    public Bean1 getObject() throws Exception {
        return new Bean1();
    }

    @Override
    public Class<?> getObjectType() {
        return Bean1.class;
    }
}
