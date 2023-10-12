package service.ms_search_engine.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import service.ms_search_engine.interceptor.MyInterceptor;


@Component
public class MyConfig implements WebMvcConfigurer {

    @Autowired
    private MyInterceptor myInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(myInterceptor).addPathPatterns("/**");
    }
}
