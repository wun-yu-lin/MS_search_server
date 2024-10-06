package service.ms_search_engine.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import service.ms_search_engine.data.BaseAuthRequest;
import java.lang.annotation.Annotation;

public class BaseAop {

    protected <T extends Annotation> Annotation getAnnotation(JoinPoint joinPoint, Class<T> clazz) {
        MethodSignature msSignature = (MethodSignature) joinPoint.getSignature();
        return msSignature.getMethod().getAnnotation(clazz);
    }

    protected BaseAuthRequest getBaseAuthReqBody(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BaseAuthRequest) {
                return (BaseAuthRequest) arg;
            }
        }
        return null;
    }

}
