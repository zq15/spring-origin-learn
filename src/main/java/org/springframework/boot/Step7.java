package org.springframework.boot;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.Map;

// banner
public class Step7 {
    public static void main(String[] args) {
        ApplicationEnvironment env = new ApplicationEnvironment();
        SpringApplicationBannerPrinter printer = new SpringApplicationBannerPrinter(
                new DefaultResourceLoader(),
                new SpringBootBanner()
        );


        // 测试文字banner
        env.getPropertySources().addLast(new MapPropertySource("custom", Map.of("spring.banner.location", "banner1.txt")));
        // 基于图片的实现
//        env.getPropertySources().addLast(new MapPropertySource("custom", Map.of("spring.banner.image.location", "banner2.png")));
        // 版本号的获取
        System.out.println(SpringBootVersion.getVersion());

        printer.print(env, Step7.class, System.out);
    }
}
