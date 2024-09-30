package service.ms_search_engine.service;

import service.ms_search_engine.exception.RedisErrorException;

public interface ServerConfigService {

    /**
     * 載入所有 SeverConfig from Redis
     */
    void loadServerConfigFromRedis() throws IllegalAccessException, RedisErrorException;

    String getServiceConfigToken();

    /**
     * 取得目前 serverConfig 設定值
     */
    String getServerConfigPropertiesString() throws IllegalAccessException;

    /**
     * 寄信給 ServerToken 給 admin, 只有 api 的 server 會寄信
     */
    String sendServerTokenToAdminMail();

}
