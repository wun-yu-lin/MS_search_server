package service.ms_search_engine.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.ms_search_engine.annotation.MSApiLock;
import service.ms_search_engine.constant.StatusCode;
import service.ms_search_engine.data.BaseRequest;
import service.ms_search_engine.exception.MsApiException;
import service.ms_search_engine.lock.MSRedisLockUtils;
import service.ms_search_engine.utility.JacksonUtils;

import java.util.Map;
import java.util.concurrent.locks.Lock;

@Aspect
@Component
public class MSApiLockAop extends BaseAop {

    private final static Logger logger = LoggerFactory.getLogger(MSApiLockAop.class);


    @Autowired
    MSRedisLockUtils msRedisLockUtils;

    @Around(value = "execution(public * service.ms_search_engine.controller..*(..))"
            + " && (@annotation(org.springframework.web.bind.annotation.RequestMapping) "
            + "    || @annotation(org.springframework.web.bind.annotation.PostMapping)"
            + "    || @annotation(org.springframework.web.bind.annotation.GetMapping))"
            + " && @annotation(service.ms_search_engine.annotation.MSApiLock)")
    public void msRedisLockTryLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MSApiLock msApiLock = (MSApiLock) getAnnotation(joinPoint, MSApiLock.class);
        BaseRequest request = getBaseReqBody(joinPoint);
        validate(msApiLock);
        Lock lock = genLock(msApiLock, request, joinPoint);
        logger.info("gen lock, lock name: {}", lock);

        try {
            if (msRedisLockUtils.tryLock(lock, msApiLock.tryLockTime())) {
                try {
                    joinPoint.proceed();
                } finally {
                    msRedisLockUtils.unlock(lock);
                }
            } else {
                throw new MsApiException(StatusCode.Base.BASE_LOCK_ERROR, "try lock fail! ");
            }
        } catch (InterruptedException e) {
            throw new MsApiException(StatusCode.Base.BASE_LOCK_ERROR, "try lock fail! ", e);
        }


    }


    private void validate(MSApiLock msApiLock) {
        if (msApiLock.reqBodyNames().length == 0 && msApiLock.reqBodyClass() == null) {
            throw new MsApiException(StatusCode.Base.BASE_PARA_ERROR, "Not allow req body class is empty");
        }

        if (msApiLock.reqBodyNames().length == 0 && msApiLock.paramNames().length == 0) {
            throw new MsApiException(StatusCode.Base.BASE_PARA_ERROR, "Not allow params is empty");
        }
    }


    private Lock genLock(MSApiLock msApiLock, BaseRequest request, JoinPoint joinPoint) throws IllegalAccessException, JsonProcessingException {
        //key argName on Method, value arg value
        Map<String, Object> argMap = getArgsMap(joinPoint);
        String[] params = msApiLock.paramNames();
        String[] reqNames = msApiLock.reqBodyNames();

        String paramLockKey = genParamLockKey(params, argMap);
        String reqNamesLockKey = genReqNamesLockKey(reqNames, request, msApiLock.reqBodyClass());

        logger.info("paramLockKey: {}", paramLockKey);
        logger.info("reqNamesLockKey: {}", reqNamesLockKey);

        return msRedisLockUtils.getLock(msApiLock.msLockGroup(), paramLockKey + reqNamesLockKey);
    }

    private String genParamLockKey(String[] params, Map<String, Object> argMap) {
        StringBuffer sb = new StringBuffer();
        for (String param : params) {
            if (argMap.containsKey(param)) {
                sb.append("_");
                sb.append(param);
                sb.append("_");
                sb.append(argMap.get(param));
            }
        }
        return sb.toString();
    }

    private String genReqNamesLockKey(String[] reqNames, BaseRequest baseRequest, Class<?> clazz) throws IllegalAccessException, JsonProcessingException {
        StringBuffer sb = new StringBuffer();
        JsonNode rootNode = JacksonUtils.getJsonRootNode(baseRequest);
        for (String reqName : reqNames) {
            JsonNode node = findNodeByPath(reqName, rootNode);
            if (node != null && node.isValueNode()) {
                String nodeText = node.asText();
                if (node instanceof NullNode || StringUtils.isEmpty(nodeText)) {
                    throw new MsApiException(StatusCode.Base.BASE_LOCK_ERROR, "Not allow lockName value is Empty String or Null");
                }
                sb.append("_");
                sb.append(reqName);
                sb.append("_");
                sb.append(nodeText);
            }
        }
        return sb.toString();
    }

    /**
     * <p>
     * ex: “data.taskId”
     * json 階層是
     * {
     * "data": {
     * "taskId": 100
     * }
     * }
     * </p>
     */
    private JsonNode findNodeByPath(String path, JsonNode rootNode) {
        if (rootNode == null) {
            return null;
        }
        String[] paths = path.split("\\.");
        JsonNode currNode = rootNode;
        for (String currPath : paths) {
            currNode = currNode.path(currPath);
            if (currNode.isMissingNode()) {
                return null;
            }
        }
        return currNode;
    }

}
