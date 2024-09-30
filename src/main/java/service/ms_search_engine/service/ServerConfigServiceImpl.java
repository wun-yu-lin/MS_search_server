package service.ms_search_engine.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.ms_search_engine.config.ServerConfig;
import service.ms_search_engine.constant.RedisConstant;
import service.ms_search_engine.exception.RedisErrorException;
import service.ms_search_engine.redisService.RedisUtil;
import service.ms_search_engine.sentMail.SentMailService;
import service.ms_search_engine.utility.JacksonUtils;

@Component
public class ServerConfigServiceImpl implements ServerConfigService {

    //log operation setting
    private final static Logger log = LoggerFactory.getLogger(ServerConfigServiceImpl.class);

    @Autowired
    SentMailService sentMailService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ServerConfig serverConfig;

    @Override
    @PostConstruct
    public void loadServerConfigFromRedis() throws IllegalAccessException, RedisErrorException {
        String redisStr = (String) redisUtil.getString(RedisConstant.SERVER_CONFIG);
        if (redisStr == null) {
            redisStr = serverConfig.getPropertiesString();
            redisUtil.setString(RedisConstant.SERVER_CONFIG, redisStr);
        }
        serverConfig.loadServerConfig(JacksonUtils.jsonToObject(redisStr, ServerConfig.class));
    }

    @Override
    public String getServiceConfigToken() {
        return serverConfig.getServerConfigToken();
    }

    @Override
    public String getServerConfigPropertiesString() throws IllegalAccessException {
        return serverConfig.getPropertiesString();
    }

    @Override
    @PostConstruct
    public String sendServerTokenToAdminMail() {

        if (serverConfig.getServerMode().isApi()) {
            String token = serverConfig.getServerConfigToken();
            String adminMail = serverConfig.getAdminMail();
            try {
                sentMailService.sendMailWithText(adminMail, "MS search server token", "token: " + token);
            } catch (Exception e) {
                log.warn("sendServerTokenToAdminMail send mail failed: ", e);
            }
        }
        return null;
    }
}
