package service.ms_search_engine.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.security.cert.TrustAnchor;


@Component
public class MyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("Invoke Interceptor' preHandler methods");

        response.setStatus(HttpStatus.BAD_GATEWAY.value());
//        return HandlerInterceptor.super.preHandle(request, response, handler);
        //do something
        return true;
    }
}
