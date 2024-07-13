package service.ms_search_engine.redisService;

import service.ms_search_engine.exception.RedisErrorException;

public interface RedisMailQueueService {
    Boolean newMail(String mailVoString) throws RedisErrorException;

    String getAndPopLastMail() throws RedisErrorException;

    Boolean queueExists() throws RedisErrorException;
}
