package service.ms_search_engine.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig extends BaseConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + serverConfig.getRedisHost() + ":" + serverConfig.getRedisPort())
                .setPassword(serverConfig.getRedisPassword())
                .setRetryAttempts(3)
                .setRetryInterval((int) serverConfig.getRedisMaxWaitMillis())
                .setConnectionMinimumIdleSize(serverConfig.getRedisMinIdle())
                .setIdleConnectionTimeout((int) serverConfig.getRedisMaxWaitMillis())
                .setConnectTimeout((int) serverConfig.getRedisMaxWaitMillis())
                .setPingConnectionInterval((int) serverConfig.getRedisMaxWaitMillis())
                .setTimeout((int) serverConfig.getRedisMaxWaitMillis())
                .setDatabase(serverConfig.getRedisDatabase());

        return Redisson.create(config);
    }

}
