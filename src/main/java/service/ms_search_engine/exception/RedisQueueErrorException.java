package service.ms_search_engine.exception;

import java.io.IOException;

public class RedisQueueErrorException extends IOException {
    public RedisQueueErrorException(String message){
        super(message);
    }
}
