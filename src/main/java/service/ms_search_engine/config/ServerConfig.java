package service.ms_search_engine.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import service.ms_search_engine.utility.JacksonUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

/**
 * 此 config 裝載所有 MS search 服務所需之設定值
 * for 動態修改需求使用
 * 目前有幾種機制寫入
 * 1. 透過 api 寫入, 但需要 tokenKey
 * 2. ansible 部署時，透過 template.properties 寫入
 */
@Component
@Scope("singleton")
@Getter
public class ServerConfig {

    //AWS
    @Value("${aws.s3.bucket.name}")
    private String awsS3BucketName;

    @Value("${aws.s3.secretKey}")
    private String awsS3SecretKey;

    @Value("${aws.s3.accessKey}")
    private String awsS3AccessKey;

    @Value("${aws.cloudFront.endpoint}")
    private String awsCloudFrontEndpoint;

    //Redis
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

    @JsonIgnore
    private String serverConfigToken;

    @JsonIgnore
    private ServerMode serverMode;

    @Value("${taskProcessorService.enable}")
    @JsonIgnore
    private Boolean taskProcessorServiceEnable;

    @Value("${SentMailTaskProcessorService.enable}")
    @JsonIgnore
    private Boolean sentMailTaskProcessorServiceEnable;

    @Value("${spring.mail.username}")
    private String adminMail;

    @Value("spring.security.admin.username")
    @JsonIgnore
    private String adminUsername;

    @Value("spring.security.admin.password")
    @JsonIgnore
    private String adminPassword;


    public enum ServerMode {
        API,
        REMOTE;
        public boolean isApi(){
            return this.equals(ServerMode.API);
        }
        public boolean isRemote(){
            return this.equals(ServerMode.REMOTE);
        }

    }

    @JsonIgnore
    public synchronized void loadServerConfig (ServerConfig serverConfig) throws IllegalAccessException {

        //object to map
        Map<String, Object> map = JacksonUtils.objectToMap(serverConfig);

        //set value to field if != null
        Class<?> clazz = this.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(JsonIgnore.class)) {
                // 取得欄位名稱和對應的值
                String fieldName = field.getName();
                if (map.get(fieldName) != null) {
                    field.set(this, map.get(fieldName));
                }
            }
        }
    }

    @PostConstruct
    private void genRandomToken() {
        setServerMode();
        if (serverMode.isApi()){
            serverConfigToken = UUID.randomUUID().toString().replace("-", "");
        }
        System.out.println("serverConfigToken: " + serverConfigToken);
    }
    @JsonIgnore
    private void setServerMode(){
        //判斷 server 的模式
        if (sentMailTaskProcessorServiceEnable) {
            serverMode = ServerMode.REMOTE;
            return;
        }
        if (taskProcessorServiceEnable) {
            serverMode = ServerMode.REMOTE;
            return;
        }
        serverMode = ServerMode.API;
    }

    @JsonIgnore
    public String getPropertiesString() throws IllegalAccessException {
        Map<String, Object> map = JacksonUtils.objectToMap(this);
        ServerConfig serverConfig = JacksonUtils.mapToObject(map, ServerConfig.class);
        return JacksonUtils.objectToJson(serverConfig);
    }



}
