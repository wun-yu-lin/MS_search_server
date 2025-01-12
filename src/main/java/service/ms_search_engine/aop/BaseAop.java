package service.ms_search_engine.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import service.ms_search_engine.data.base.BaseAuthRequestData;
import service.ms_search_engine.data.base.BaseRequestData;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class BaseAop {

    protected <T extends Annotation> Annotation getAnnotation(JoinPoint joinPoint, Class<T> clazz) {
        MethodSignature msSignature = (MethodSignature) joinPoint.getSignature();
        return msSignature.getMethod().getAnnotation(clazz);
    }

    protected MethodSignature getMethodSignature(JoinPoint joinPoint) {
        return (MethodSignature) joinPoint.getSignature();
    }

    protected BaseAuthRequestData getBaseAuthReqBody(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BaseAuthRequestData) {
                return (BaseAuthRequestData) arg;
            }
        }
        return null;
    }

    /**
     * key argName on Method, value arg value
     */
    protected Map<String, Object> getArgsMap(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = getMethodSignature(joinPoint);
        String[] parameterNames = signature.getParameterNames();

        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            map.put(parameterNames[i], args[i]);
        }

        return map;
    }


    protected BaseRequestData getBaseReqBody(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BaseRequestData) {
                return (BaseRequestData) arg;
            }
        }
        return null;
    }

}
