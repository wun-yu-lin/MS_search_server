package service.ms_search_engine.exception;

import java.io.IOException;

public class RedisErrorException extends IOException {
    public RedisErrorException(String message){
        super(message);
    }
}
