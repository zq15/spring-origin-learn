package com.example.boot.A41;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 1. 按需加载不同类型 dataSource
 * 来源: class path resource [org/springframework/boot/autoconfigure/jdbc/DataSourceConfiguration$Hikari.class]
 *@Configuration(proxyBeanMethods = false)
 * 	    @Conditional(PooledDataSourceCondition.class)
 *    @ConditionalOnMissingBean({ DataSource.class, XADataSource.class })
 *    @Import({ DataSourceConfiguration.Hikari.class, DataSourceConfiguration.Tomcat.class,
 * 			DataSourceConfiguration.Dbcp2.class, DataSourceConfiguration.OracleUcp.class,
 * 			DataSourceConfiguration.Generic.class, DataSourceJmxConfiguration.class })
 * 	protected static class PooledDataSourceConfiguration {
 *
 *    }
 * DataSourceConfiguration 中也是用条件注解判断，这里我们有 springboot-jdbc 默认引入了 hikari 的依赖，
 * 所以满足hikari数据源的条件，其他数据源类型因为缺少依赖不能创建
 * 2. @EnableConfigurationProperties(DataSourceProperties.class) 拿到 spring.datasource 相关信息并绑定
 *
 * @ConfigurationProperties(prefix = "spring.datasource")
 * public class DataSourceProperties implements BeanClassLoaderAware, InitializingBean {
 *
 * 后面的 DataSource 创建也会引入这里的配置信息
 * protected static <T> T createDataSource(DataSourceProperties properties, Class<? extends DataSource> type) {
 * 		return (T) properties.initializeDataSourceBuilder().type(type).build();
 * 	    }
 * 3. MybatisAutoConfiguration
 * 引入了mybatis 肯定有
 * @ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
 * 有且仅有一个 DataSource
 * @ConditionalOnSingleCandidate(DataSource.class)
 * 属性对象绑定
 * @EnableConfigurationProperties({MybatisProperties.class})
 * 解析顺序配置，在 DataSourceAutoConfiguration 之后
 * @AutoConfigureAfter({DataSourceAutoConfiguration.class, MybatisLanguageDriverAutoConfiguration.class})
 *
 * 3.1 注册 SqlSessionFactory
 *     @Bean
 *     @ConditionalOnMissingBean
 *     public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
 * 3.2 注册 SqlSessionTemplate -> 创建一个和线程绑定的 SqlSession -> MapperFactoryBean中调用逻辑 见下面代码
 * public T getObject() throws Exception {
 *         return this.getSqlSession().getMapper(this.mapperInterface);
 *     }
 *public SqlSession getSqlSession() {
 *         return this.sqlSessionTemplate;
 *     }
 * 4. mapper 扫描 bean
 *     @Configuration(
 *         proxyBeanMethods = false
 *     )
 *     @Import({AutoConfiguredMapperScannerRegistrar.class})
 *     @ConditionalOnMissingBean({MapperFactoryBean.class, MapperScannerConfigurer.class})
 *     public static class MapperScannerRegistrarNotFoundConfiguration implements InitializingBean {
 *
 *    -> AutoConfiguredMapperScannerRegistrar -> 实现 ImportBeanDefinitionRegistrar
 *         public static class AutoConfiguredMapperScannerRegistrar implements BeanFactoryAware, EnvironmentAware, ImportBeanDefinitionRegistrar {
 *

 *
 */
public class TestDataSourceAuto {

    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        StandardEnvironment env = new StandardEnvironment();
        env.getPropertySources().addLast(new SimpleCommandLinePropertySource(
                "--spring.datasource.url=jdbc:mysql://localhost:3306/test",
                "--spring.datasource.username=root",
                "--spring.datasource.password=123456"
        ));

        context.setEnvironment(env);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context.getDefaultListableBeanFactory());
        context.registerBean(Config.class);

        // 添加 mapper 包扫描路径
        AutoConfigurationPackages.register(context.getDefaultListableBeanFactory(), "com.example.boot.A41.mapper");
        context.refresh();

        for (String name : context.getBeanDefinitionNames()) {
            if (context.getBeanDefinition(name).getResourceDescription() != null)
              System.out.println(name + " 来源: " + context.getBeanDefinition(name).getResourceDescription());
        }
        // 测试 DataSourceProperties 中绑定信息
        DataSourceProperties properties = context.getBean(DataSourceProperties.class);
        System.out.println(properties.getDriverClassName());
        System.out.println(properties.getUrl());
        System.out.println(properties.getUsername());
        System.out.println(properties.getPassword());
        context.close();
    }

    @Configuration
    @Import(MyImportSelector.class)
    static class Config {

    }

    static class MyImportSelector implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{
                    DataSourceAutoConfiguration.class.getName(),
                    MybatisAutoConfiguration.class.getName(),
                    DataSourceTransactionManagerAutoConfiguration.class.getName(),
                    TransactionAutoConfiguration.class.getName()
            };
        }
    }
}
