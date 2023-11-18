package service.ms_search_engine.redisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import service.ms_search_engine.exception.RedisErrorException;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    private static final Long SUCCESS = 1L;

    private RedisTemplate redisTemplate;

    @Autowired
    public RedisUtil(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    //---------------------- common --------------------------

    /**
     * 指定緩存失效時間
     *
     * @param key  key值
     * @param time 緩存時間
     */
    public void expire(String key, long time) throws RedisErrorException {
        if (time > 0) {
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        } else {
            throw new RedisErrorException("插入List緩存失敗！" + key);
        }
    }

    /**
     * 判斷key是否存在
     *
     * @param key 傳入ke值
     * @return true 存在  false  不存在
     */
    public Boolean existsKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 判斷key存儲的值類型
     *
     * @param key key值
     * @return DataType[string、list、set、zset、hash]
     */
    public DataType typeKey(String key) {
        return redisTemplate.type(key);
    }

    /**
     * 刪除指定的一個數據
     *
     * @param key key值
     * @return true 刪除成功，否則返回異常信息
     */
    public Boolean deleteKey(String key) throws RedisErrorException {
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Exception ex) {
            throw new RedisErrorException("插入List緩存失敗！" + key);
        }
    }

    /**
     * 刪除多個數據
     *
     * @param keys key的集合
     * @return true刪除成功，false刪除失敗
     */
    public Boolean deleteKey(Collection<String> keys) throws RedisErrorException {
        try {
            redisTemplate.delete(keys);
            return true;
        } catch (Exception ex) {
            throw new RedisErrorException("插入List緩存失敗！" + keys.toString());
        }
    }

    //-------------------- String ----------------------------

    /**
     * 普通緩存放入
     *
     * @param key   鍵值
     * @param value 值
     * @return true成功 要麼異常
     */
    public Boolean setString(String key, Object value) throws RedisErrorException {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception ex) {
            throw new RedisErrorException("插入List緩存失敗！" + key);
        }
    }

    /**
     * 普通緩存獲取
     *
     * @param key 鍵
     * @return 值
     */
    public Object getString(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 設置緩存存在時間
     *
     * @param key   key值
     * @param value value值
     * @param time  時間 秒爲單位
     * @return 成功返回true，失敗返回異常信息
     */
    public boolean setString(String key, Object value, long time) throws RedisErrorException {
        try {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            return true;
        } catch (Exception ex) {
            throw new RedisErrorException("插入List緩存失敗！" + key);
        }
    }


    //-----------------------------hash----------------------------------

    /**
     * 設置hash值,並設置過期時間
     *
     * @param key
     * @param hk
     * @param hv
     * @param time
     * @return
     */
    public Boolean setHash(String key, Object hk, Object hv, long time) {
        redisTemplate.opsForHash().put(key, hk, hv);
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
        return true;
    }

    public Boolean setHash(String key, Map map, long time) {
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
        return true;
    }

    /**
     * 獲取hash的值
     *
     * @param key
     * @param hk
     * @return
     */
    public Object getHash(String key, String hk) {
        return key == null ? null : (hk == null ? null : redisTemplate.opsForHash().get(key, hk));
    }

    /**
     * hash累加
     */
    public Long hincrease(String key, String hk, long l) {
        return redisTemplate.opsForHash().increment(key, hk, l);
    }

    //----------------------------- list ------------------------------

    /**
     * 將list放入緩存
     *
     * @param key   key的值
     * @param value 放入緩存的數據
     * @return true 代表成功，否則返回異常信息
     */
    public Boolean setList(String key, Object value) throws RedisErrorException {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception ex) {
            throw new RedisErrorException("插入List緩存失敗！" + key);
        }
    }

    /**
     * 將Object數據放入List緩存，並設置時間
     *
     * @param key   key值
     * @param value 數據的值
     * @param time  緩存的時間
     * @return true插入成功，否則返回異常信息
     */
    public Boolean setList(String key, Object value, long time) throws RedisErrorException {
        try {
            if (time > 0) {
                redisTemplate.opsForList().rightPush(key, value);
                expire(key, time);
                return true;
            }
            return false;
        } catch (Exception ex) {
            throw new RedisErrorException("插入List緩存失敗！" + key);
        }
    }

    /**
     * 將list集合放入List緩存，並設置時間
     *
     * @param key   key值
     * @param value 數據的值
     * @param time  緩存的時間
     * @return true插入成功，否則返回異常信息
     */
    public Boolean setListAll(String key, Object value, long time) throws RedisErrorException {
        try {
            if (time > 0) {
                redisTemplate.opsForList().rightPushAll(key, value);
                this.expire(key, time);
                return true;
            }
            return false;
        } catch (Exception ex) {
            throw new RedisErrorException("插入List緩存失敗！" + key);
        }
    }

    /**
     * 根據索引獲取緩存List中的內容
     *
     * @param key   key的值
     * @param start 索引開始
     * @param end   索引結束 0 到 -1代表所有值
     * @return 返回數據
     */
    public List<Object> getList(String key, long start, long end) throws RedisErrorException {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception ex) {
            throw new RedisErrorException("獲取緩存List中的內容失敗了！" + key);
        }
    }

    /**
     * 根據List head寫入緩存List中的內容
     *
     * @param key   key的值
     * @param value 要寫入 list 中的內容
     * @return 返回數據
     */
    public Boolean setListHead(String key, Object value) throws RedisErrorException {
        try {
            redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception ex) {
            throw new RedisErrorException("獲取緩存List中的內容失敗了！" + key);
        }
        return true;
    }

    /**
     * 根據List tail取得緩存List中的內容
     *
     * @return 返回數據
     */
    public Object getAndPopListTail(String key) throws RedisErrorException {
        try {
            long size = redisTemplate.opsForList().size(key);
            if (size == 0) {
                return null;
            }
            Object value = redisTemplate.opsForList().index(key, size - 1);
            redisTemplate.opsForList().rightPop(key);
            return value;
        } catch (Exception ex) {
            throw new RedisErrorException("獲取緩存List中的內容失敗了！" + key);
        }
    }


    /**
     * 刪除List緩存中多個list數據
     *
     * @param key   key值
     * @param count 移除多少個
     * @param value 可以傳null  或者傳入存入的Value的值
     * @return 返回刪除了多少個
     */
    public long deleteListIndex(String key, long count, Object value) throws RedisErrorException {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception ex) {
            throw new RedisErrorException("刪除List中的內容失敗了！" + key);
        }

    }

    /**
     * 獲取List緩存的數據
     *
     * @param key key值
     * @return 返回長度
     */
    public long getListSize(String key) throws RedisErrorException {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception ex) {
            throw new RedisErrorException("獲取List長度失敗" + key);
        }
    }


    //----------------------set-------------------

    /**
     * 判斷是否包含在Set中
     *
     * @param key
     * @param o
     */
    public void isContainsKey(String key, HashSet o) {
        redisTemplate.opsForSet().isMember(key, o);
    }

    //-----------------------lock----------------------

    /**
     * 獲取分佈式鎖
     *
     * @param lockKey     鎖
     * @param requestId   請求標識
     * @param expireTime  單位秒
     * @param waitTimeout 單位毫秒
     * @return 是否獲取成功
     */
    public boolean tryLock(String lockKey, String requestId, int expireTime, long waitTimeout) {
        // 當前時間
        long nanoTime = System.nanoTime();
        try {
            String script = "if redis.call('setNx',KEYS[1],ARGV[1]) then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";
            int count = 0;
            do {
                RedisScript<String> redisScript = new DefaultRedisScript<>(script, String.class);

                Object result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId, expireTime);

                if (SUCCESS.equals(result)) {
                    return true;
                }
                //休眠500毫秒
                Thread.sleep(500L);
                count++;
            } while ((System.nanoTime() - nanoTime) < TimeUnit.MILLISECONDS.toNanos(waitTimeout));

        } catch (Exception e) {
            System.out.println("嘗試獲取分佈式鎖-key[{}]異常" + lockKey);
        }

        return false;
    }


    /**
     * 釋放鎖
     *
     * @param lockKey   鎖
     * @param requestId 請求標識
     * @return 是否釋放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

        RedisScript<String> redisScript = new DefaultRedisScript<>(script, String.class);

        Object result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
        if (SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }


}
