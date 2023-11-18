package service.ms_search_engine.redisService;

import service.ms_search_engine.exception.RedisErrorException;

public interface RedisTaskQueueService {
    Boolean newTask(String taskString) throws RedisErrorException;
    String getAndPopLastTask() throws RedisErrorException;

    Boolean queueExists() throws RedisErrorException;



}
