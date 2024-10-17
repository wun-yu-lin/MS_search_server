package service.ms_search_engine.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import service.ms_search_engine.data.BaseAuthRequest;
import service.ms_search_engine.data.BaseRequest;

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

    protected BaseAuthRequest getBaseAuthReqBody(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BaseAuthRequest) {
                return (BaseAuthRequest) arg;
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


    protected BaseRequest getBaseReqBody(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BaseRequest) {
                return (BaseRequest) arg;
            }
        }
        return null;
    }

}
