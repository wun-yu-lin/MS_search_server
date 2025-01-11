package service.ms_search_engine.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import service.ms_search_engine.annotation.NoLogging;
import service.ms_search_engine.constant.StatusCode;
import service.ms_search_engine.exception.MsApiException;
import service.ms_search_engine.utility.JacksonUtils;

import java.lang.reflect.Field;
import java.util.Map;

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

    @JsonIgnore
    @NoLogging
    private final static Logger logger = LoggerFactory.getLogger(ServerConfig.class);

    //AWS
    @Value("${aws.s3.bucket.name}")
    private String awsS3BucketName;

    @Value("${aws.s3.secretKey}")
    @NoLogging
    private String awsS3SecretKey;

    @Value("${aws.s3.accessKey}")
    @NoLogging
    private String awsS3AccessKey;

    @Value("${aws.cloudFront.endpoint}")
    private String awsCloudFrontEndpoint;

    //Redis
    @Value("${redis.taskQueue.host}")
    private String redisHost;

    @Value("${redis.taskQueue.port}")
    private int redisPort;

    @Value("${redis.taskQueue.password}")
    @NoLogging
    private String redisPassword;

    @Value("${redis.taskQueue.database}")
    private int redisDatabase;

    @Value("${redis.taskQueue.maxWaitMillis}")
    private long redisMaxWaitMillis;

    @Value("${redis.taskQueue.maxIdle}")
    private int redisMaxIdle;

    @Value("${redis.taskQueue.minIdle}")
    private int redisMinIdle;

    @Value("${redis.taskQueue.maxTotal}")
    private int redisMaxTotal;

    @JsonIgnore
    @Value("${server.serverConfigToken}")
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

    @Value("${spring.security.admin.username}")
    @JsonIgnore
    private String adminUsername;

    @Value("${spring.security.admin.password}")
    @JsonIgnore
    @NoLogging
    private String adminPassword;

    //開頭需要是PROD, SIT, DEV
    @JsonIgnore
    @Value("${server.deployEnvironment}")
    private String deployEnvironment = "DEV";

    @JsonIgnore
    public DeployEnv getDeployEnv(){
        if (StringUtils.startsWith(deployEnvironment, "PROD")) {
            return DeployEnv.PRODUCTION;
        }
        if (StringUtils.startsWith(deployEnvironment, "SIT")) {
            return DeployEnv.SIT;
        }
        if (StringUtils.startsWith(deployEnvironment, "DEV")) {
           return DeployEnv.DEV;
        }
        if (StringUtils.startsWith(deployEnvironment, "TEST")) {
            return DeployEnv.TEST_API_WITH_NO_AUTH;
        }

        throw new MsApiException(StatusCode.Base.BASE_PARA_ERROR, "Not allow deploy environment setting");
    }

    public enum DeployEnv {
        DEV,
        SIT,
        PRODUCTION,
        TEST_API_WITH_NO_AUTH;
        public boolean isDev(){
            return this == DeployEnv.DEV;
        }
        public boolean isProd(){
            return this == DeployEnv.PRODUCTION;
        }

        public boolean isSit(){
            return this == DeployEnv.SIT;
        }
    }



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

    @JsonIgnore
    @PostConstruct
    public void setServerMode(){
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

    @JsonIgnore
    public void loggingAllConfig(){
        Class<?> clazz = this.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(NoLogging.class)) {
                // 取得欄位名稱和對應的值
                String fieldName = field.getName();
                String fieldValue = null;
                try {
                    fieldValue = String.valueOf(field.get(this));
                } catch (Exception e) {
                   logger.warn("get field: {} failed", fieldName);
                }
                logger.info("serverConfig: {}={}", fieldName, fieldValue);
            }
        }

    }



}
