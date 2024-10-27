package service.ms_search_engine.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final static Logger logger = LoggerFactory.getLogger(ServerConfigServiceImpl.class);

    @Autowired
    SentMailService sentMailService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ServerConfig serverConfig;

    @Override
    @PostConstruct
    public void initServerConfig() throws IllegalAccessException {
        logger.info("initServerConfig...");
        loadServerConfigFromRedis();
        serverConfig.loggingAllConfig();
    }

    @Override
    @Scheduled(cron = "0 */15 * * * ?") // 每 15 分鐘刷新一次
    public void loadServerConfigFromRedis() throws IllegalAccessException, RedisErrorException {
        String redisStr = (String) redisUtil.getString(RedisConstant.SERVER_CONFIG);
        if (redisStr == null) {
            redisStr = serverConfig.getPropertiesString();
            redisUtil.setString(RedisConstant.SERVER_CONFIG, redisStr);
        }
        serverConfig.loadServerConfig(JacksonUtils.jsonToObject(redisStr, ServerConfig.class));
    }

    @Override
    public void uploadAndLoadServerConfigToRedis(ServerConfig updateConfig) throws IllegalAccessException, RedisErrorException {
        serverConfig.loadServerConfig(updateConfig);
        String redisStr = serverConfig.getPropertiesString();
        redisUtil.setString(RedisConstant.SERVER_CONFIG, redisStr);
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
    public void sendServerTokenToAdminMail() {

        if (!serverConfig.getServerMode().isApi()) {
            return;
        }
        ServerConfig.DeployEnv deployEnv = serverConfig.getDeployEnv();
        if (!deployEnv.isProd() && !deployEnv.isSit()) {
            return;
        }

        String token = serverConfig.getServerConfigToken();
        String adminMail = serverConfig.getAdminMail();
        try {
            sentMailService.sendMailWithText(adminMail, "MS search server token, deploy env: " + serverConfig.getDeployEnvironment(), "token: " + token);
        } catch (Exception e) {
            logger.warn("sendServerTokenToAdminMail send mail failed: ", e);
        }
    }
}
