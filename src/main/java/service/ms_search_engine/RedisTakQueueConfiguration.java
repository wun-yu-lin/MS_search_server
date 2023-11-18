package service.ms_search_engine;


import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisTakQueueConfiguration {

    @Value("${redis.taskQueue.host}")
    private String redisTaskQueueHost;

    @Value("${redis.taskQueue.port}")
    private int redisTaskQueuePort;

    @Value("${redis.taskQueue.password}")
    private String redisTaskQueuePassword;

    @Value("${redis.taskQueue.database}")
    private int redisTaskQueueDatabase;

    @Value("${redis.taskQueue.maxWaitMillis}")
    private long redisTaskQueueMaxWaitMillis;

    @Value("${redis.taskQueue.maxIdle}")
    private int redisTaskQueueMaxIdle;

    @Value("${redis.taskQueue.minIdle}")
    private int redisTaskQueueMinIdle;

    @Value("${redis.taskQueue.maxTotal}")
    private int redisTaskQueueMaxTotal;




    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisTaskQueueHost);
        config.setPort(redisTaskQueuePort); // Redis的預設埠號
        config.setPassword(redisTaskQueuePassword); // 放Redis的密碼，這裡暫時沒有設
        config.setDatabase(redisTaskQueueDatabase);
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxWaitMillis(redisTaskQueueMaxWaitMillis); // 當連線取完時，欲取得連線的最大的等待時間
        poolConfig.setMaxIdle(redisTaskQueueMaxIdle); // 最大空閒連線數
        poolConfig.setMinIdle(redisTaskQueueMinIdle); // 最小空閒連線數
        poolConfig.setMaxTotal(redisTaskQueueMaxTotal); // 最大連線數

        LettucePoolingClientConfiguration poolingClientConfig =
                LettucePoolingClientConfiguration.builder()
                        .commandTimeout(Duration.ofMillis(redisTaskQueueMaxWaitMillis))
                        .poolConfig(poolConfig)
                        .build();




        return new LettuceConnectionFactory(config, poolingClientConfig);
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setDefaultSerializer(
                new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
