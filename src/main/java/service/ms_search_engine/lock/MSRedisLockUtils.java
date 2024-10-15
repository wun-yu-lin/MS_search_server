package service.ms_search_engine.lock;

import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.ms_search_engine.constant.StatusCode;
import service.ms_search_engine.exception.MsApiException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

@Component
public class MSRedisLockUtils  {

    private final static Logger logger = LoggerFactory.getLogger(MSRedisLockUtils.class);

    private static final String REDIS_LOCK_TITLE  = "REDIS_LOCK_";

    @Autowired
    private RedissonClient redissonClient;

    public enum MSLockGroup {
        MSMS_JOB_QUEUE_BY_TASK_ID,
        SUMMIT_TASK_BY_TASK_ID

    }

    public Lock getLock(MSLockGroup msLockGroup, String lockKey) {
        String lockName = genLockName(msLockGroup, lockKey);
        logger.info("getRLock, lockName: {}", lockName);
        return redissonClient.getLock(lockName);
    }

    public ReadWriteLock getReadWriteLock(MSLockGroup msLockGroup, String lockKey) {
        String lockName = genLockName(msLockGroup, lockKey);
        logger.info("getReadWriteLock, lockName: {}", lockName);
        System.out.println();
        return redissonClient.getReadWriteLock(lockName);
    }

    public RedissonMultiLock getMultiLock(RLock... locks) throws MsApiException {
        checkMultiLock(locks);
        logger.info("getMultiLock, locks: {}", Arrays.toString(locks));
        return (RedissonMultiLock) redissonClient.getMultiLock(locks);
    }

    public boolean tryLock(Lock lock, long microSecondTimeOut) throws InterruptedException {
        logger.info("tryLock, lockName: {}", lock);
        return lock.tryLock(microSecondTimeOut, TimeUnit.MICROSECONDS);
    }

    public void unlock(Lock lock) {
        logger.info("Unlock, lockName: {}", lock);
        lock.unlock();
    }


    public void executeWithLock(MSLockGroup msLockGroup, String lockKey, long microSecondTimeOut, Runnable cb) throws MsApiException {
        Lock lock = getLock(msLockGroup, lockKey);
        logger.info("tryLockWithExecute, lockName: {}", lock);
        try {
            if (lock.tryLock(microSecondTimeOut, TimeUnit.MICROSECONDS)) {
                try {
                    cb.run();
                } finally {
                    lock.unlock();
                }
            } else {
                logger.info("tryLockWithExecute failed! lockName: {}", lock);
                throw new MsApiException(StatusCode.Base.BASE_LOCK_ERROR, "try lock failed");
            }
        } catch (InterruptedException e) {
            logger.info("tryLockWithExecute failed! ", e);
            throw new MsApiException(StatusCode.Base.BASE_LOCK_ERROR, "try lock failed");
        }
    }

    private static String genLockName(MSLockGroup msLockGroup, String lockKey){
        StringBuffer sb = new StringBuffer();
        sb.append(REDIS_LOCK_TITLE);
        if (msLockGroup != null) {
            sb.append(msLockGroup.name());
            sb.append("_");
        }
        if (lockKey == null || lockKey.isEmpty()) {
            throw new IllegalArgumentException("Not allow empty key");
        }
        sb.append(lockKey);
        return sb.toString();
    }

    //不允許有相同 lock name 的 lock
    private void checkMultiLock(RLock... locks) throws MsApiException {
        Set<String> lockNameSet = new HashSet<>();
        for (RLock lock : locks) {
            if (lockNameSet.contains(lock.getName())) {
                throw new MsApiException(StatusCode.Base.BASE_LOCK_ERROR, "Not allow same lock name");
            }
            lockNameSet.add(lock.getName());
        }
    }

}
