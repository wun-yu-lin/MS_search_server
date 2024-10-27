package service.ms_search_engine.service;

import service.ms_search_engine.config.ServerConfig;
import service.ms_search_engine.exception.RedisErrorException;

public interface ServerConfigService {

    /**
     * server init 執行的 method, 不可由任何其他來源 invoke
     */
    void initServerConfig() throws IllegalAccessException;


    /**
     * 載入所有 SeverConfig from Redis
     */
    void loadServerConfigFromRedis() throws IllegalAccessException, RedisErrorException;


    /**
     * 寫入有提供的參數到 redis, 並刷新 serverConfig
     */
    void uploadAndLoadServerConfigToRedis(ServerConfig serverConfig) throws IllegalAccessException, RedisErrorException;

    String getServiceConfigToken();

    /**
     * 取得目前 serverConfig 設定值
     */
    String getServerConfigPropertiesString() throws IllegalAccessException;

    /**
     * 寄信給 ServerToken 給 admin, 只有 api 的 server 會寄信
     */
    void sendServerTokenToAdminMail();

}
