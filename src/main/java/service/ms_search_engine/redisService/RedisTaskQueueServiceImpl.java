package service.ms_search_engine.redisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.ms_search_engine.exception.RedisErrorException;

@Component
public class RedisTaskQueueServiceImpl implements RedisTaskQueueService{
    private final RedisUtil redisUtil;

    @Autowired
    public RedisTaskQueueServiceImpl(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    public Boolean newTask(String taskString) throws RedisErrorException {

        return redisUtil.setListHead("taskQueue", taskString);
    }

    @Override
    public String getAndPopLastTask() throws RedisErrorException {
        Object lastTaskValue = redisUtil.getAndPopListTail("taskQueue");
        return (String) lastTaskValue;
    }


    @Override
    public Boolean queueExists() throws RedisErrorException {
       return redisUtil.isListExist("taskQueue");
    }


}
