package service.ms_search_engine.exception;

import service.ms_search_engine.constant.StatusCode;

public class RedisErrorException extends MsApiException {
    public RedisErrorException(String message){
        super(StatusCode.Base.BASE_REDIS_ERROR, message);
    }

    public RedisErrorException(String message, Throwable e){
        super(StatusCode.Base.BASE_REDIS_ERROR, message, e);
    }
}
