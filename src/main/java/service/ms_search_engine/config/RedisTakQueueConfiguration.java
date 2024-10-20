package service.ms_search_engine.config;


import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisTakQueueConfiguration extends BaseConfig {


    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(serverConfig.getRedisHost());
        config.setPort(serverConfig.getRedisPort()); // Redis的預設埠號
        config.setPassword(serverConfig.getRedisPassword()); // 放Redis的密碼，這裡暫時沒有設
        config.setDatabase(serverConfig.getRedisDatabase());
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxWaitMillis(serverConfig.getRedisMaxWaitMillis()); // 當連線取完時，欲取得連線的最大的等待時間
        poolConfig.setMaxIdle(serverConfig.getRedisMaxIdle()); // 最大空閒連線數
        poolConfig.setMinIdle(serverConfig.getRedisMinIdle()); // 最小空閒連線數
        poolConfig.setMaxTotal(serverConfig.getRedisMaxTotal()); // 最大連線數

        LettucePoolingClientConfiguration poolingClientConfig =
                LettucePoolingClientConfiguration.builder()
                        .commandTimeout(Duration.ofMillis(serverConfig.getRedisMaxWaitMillis()))
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
