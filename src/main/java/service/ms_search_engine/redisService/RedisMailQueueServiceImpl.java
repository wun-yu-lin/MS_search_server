package service.ms_search_engine.redisService;

import org.springframework.stereotype.Component;
import service.ms_search_engine.exception.RedisErrorException;


@Component
public class RedisMailQueueServiceImpl implements RedisMailQueueService{

    private final RedisUtil redisUtil;

    public RedisMailQueueServiceImpl(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    public Boolean newMail(String mailVoString) throws RedisErrorException {
        return redisUtil.setListHead("mailQueue", mailVoString);

    }

    @Override
    public String getAndPopLastMail() throws RedisErrorException {
        Object lastMailValue = redisUtil.getAndPopListTail("mailQueue");
        return (String) lastMailValue;
    }

    @Override
    public Boolean queueExists() throws RedisErrorException {
        return redisUtil.isListExist("mailQueue");
    }
}
