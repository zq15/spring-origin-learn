package com.example.boot.A21;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPart;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMapMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;

/**
 * 目标: 解析 控制器方法的参数值
 *
 * >>>>>>>>>>>>>> 所有参数解析器
 * org.springframework.web.method.annotation.RequestParamMethodArgumentResolver@3b9632d1
 * org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver@4e6f2bb5
 * org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver@21e20ad5
 * org.springframework.web.servlet.mvc.method.annotation.PathVariableMapMethodArgumentResolver@3f628ce9
 * org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMethodArgumentResolver@35e8316e
 * org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMapMethodArgumentResolver@26d96e5
 * org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor@336880df
 * org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor@1846579f
 * org.springframework.web.servlet.mvc.method.annotation.RequestPartMethodArgumentResolver@6cd166b8
 * org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver@2650f79
 * org.springframework.web.method.annotation.RequestHeaderMapMethodArgumentResolver@75fc1992
 * org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver@5fac521d
 * org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver@38af1bf6
 * org.springframework.web.servlet.mvc.method.annotation.SessionAttributeMethodArgumentResolver@129bd55d
 * org.springframework.web.servlet.mvc.method.annotation.RequestAttributeMethodArgumentResolver@7be7e15
 * org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver@3abfe845
 * org.springframework.web.servlet.mvc.method.annotation.ServletResponseMethodArgumentResolver@7a0f244f
 * org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor@3672276e
 * org.springframework.web.servlet.mvc.method.annotation.RedirectAttributesMethodArgumentResolver@4248b963
 * org.springframework.web.method.annotation.ModelMethodProcessor@7f08caf
 * org.springframework.web.method.annotation.MapMethodProcessor@4defd42
 * org.springframework.web.method.annotation.ErrorsMethodArgumentResolver@2330e3e0
 * org.springframework.web.method.annotation.SessionStatusMethodArgumentResolver@24b4d544
 * org.springframework.web.servlet.mvc.method.annotation.UriComponentsBuilderMethodArgumentResolver@27a2a089
 * com.example.boot.A20.TokenArgumentResolver@54657dd2
 * org.springframework.web.servlet.mvc.method.annotation.PrincipalMethodArgumentResolver@706eab5d
 * org.springframework.web.method.annotation.RequestParamMethodArgumentResolver@72725ee1
 * org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor@40e60ece
 */
public class A21 {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(WebConfig.class);
        HttpServletRequest multiRequest = mockRequest();

        // 要点1: 控制器方法被封装为 HandlerMethod
        HandlerMethod handlerMethod = new HandlerMethod(new Controller(), Controller.class.getMethod("test", String.class, String.class, int.class, String.class,
            MultipartFile.class, int.class, String.class, String.class, String.class, HttpServletRequest.class, User.class, User.class, User.class));

        // 要点2: 准备对象绑定与方法转换
        ServletRequestDataBinderFactory dataBinderFactory = new ServletRequestDataBinderFactory(null, null);

        // 要点3: 准备 ModelAndViewController 用来存储中间 Model 结果
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();

        // 要点4: 解析每个参数值
        for (MethodParameter parameter : handlerMethod.getMethodParameters()) {

            // 多个解析器组合
            HandlerMethodArgumentResolverComposite composite = new HandlerMethodArgumentResolverComposite();
            composite.addResolvers(
                // false 表示必须要有 @RequestParam 注解
                new RequestParamMethodArgumentResolver(context.getBeanFactory(), false), // 提供 BeanFactory 后，可以解析环境变量
                new PathVariableMethodArgumentResolver(),
                new RequestHeaderMethodArgumentResolver(context.getBeanFactory()),
                new ServletCookieValueMethodArgumentResolver(context.getBeanFactory()),
                new ExpressionValueMethodArgumentResolver(context.getBeanFactory()),
                new ServletRequestMethodArgumentResolver(),
                new ServletModelAttributeMethodProcessor(false), // 必须有 @ModelAttribute 注解
                new RequestResponseBodyMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())), // 不能调整最后两个的位置
                new ServletModelAttributeMethodProcessor(true), // 省略 @ModelAttribute 注解
                new RequestParamMethodArgumentResolver(context.getBeanFactory(), true)
            );

            String annotations = Arrays.stream(parameter.getParameterAnnotations()).map(annotation -> annotation.annotationType().getSimpleName()).collect(Collectors.joining());
            String str = !annotations.isEmpty() ? " @" + annotations + " " : " ";
            parameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());

            if (composite.supportsParameter(parameter)) {
                // 支持此参数
                Object v = composite.resolveArgument(parameter, mavContainer, new ServletWebRequest(multiRequest), dataBinderFactory);
//                System.out.println(v.getClass()); // 18 类型是 String 没有类型转换，添加 dataBinderFactory 后 转换成功
                System.out.println("[" + parameter.getParameterIndex() +"] " + str + parameter.getParameterType().getSimpleName() + " " + parameter.getParameterName() + " -> " + v);
                System.out.println("模型数据为: " + mavContainer.getModel());
            } else {
                System.out.println("[" + parameter.getParameterIndex() +"] " + str + parameter.getParameterType().getSimpleName() + " " + parameter.getParameterName());
            }

        }
    }

    private static HttpServletRequest mockRequest() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("name1", "zhangsan");
            request.setParameter("name2", "lisi");
            request.addPart(new MockPart("file", "abc", "hello".getBytes(StandardCharsets.UTF_8)));
            Map<String, String> map = new AntPathMatcher()
                .extractUriTemplateVariables("/test/{id}", "/test/123");
            request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, map);
            request.setContentType("application/json");
            request.setCookies(new Cookie("token", "123456"));
            request.setParameter("name", "张三");
            request.setParameter("age", "18");
            request.setContent("""
                {
                    "name": "lisi",
                    "age": 18
                }
                """.getBytes(StandardCharsets.UTF_8));
            return new StandardServletMultipartResolver().resolveMultipart(request);
    }

    static class Controller {
        public void test(
            @RequestParam("name1") String name1, // name = zhangsan
            String name2, // 同上
            @RequestParam("age") int age, // 数据类型转换
            @RequestParam(name = "home", defaultValue = "${JAVA_HOME}") String home1, // 取参数默认值，spring获取数据
            @RequestParam("file") MultipartFile file, // 上传文件
            @PathVariable("id") int id, // test/123
            @RequestHeader("Content-Type") String header,
            @CookieValue("token") String token,
            @Value("${JAVA_HOME}") String home2, // spring获取数据
            HttpServletRequest request, // request, response, session
            @ModelAttribute("abc") User user1, // name=zhangsan&age=18 和数据做一个绑定，并且把结果作为一个模型数据
            User user2, // name=zhangsan&age=18
            @RequestBody User user3 // json
        ) {}
    }

    public static class User{
        private String name;
        private Integer age;

        public User() {
        }

        public User(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
        }
    }

}
