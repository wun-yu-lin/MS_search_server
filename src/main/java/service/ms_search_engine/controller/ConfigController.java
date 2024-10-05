package service.ms_search_engine.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ms_search_engine.config.ServerConfig;
import service.ms_search_engine.constant.StatusCode;
import service.ms_search_engine.data.ServerConfigReq;
import service.ms_search_engine.data.ServerConfigResp;
import service.ms_search_engine.exception.MsApiException;
import service.ms_search_engine.service.ServerConfigService;

@RestController
@RequestMapping("/api/config")
public class ConfigController extends BaseController {


    @Autowired
    private ServerConfigService serverConfigService;


    private void checkServerToken(String token) throws MsApiException {
        if (serverConfig.getServerConfigToken() == null) {
            throw new MsApiException(StatusCode.Base.BASE_PARA_ERROR, "server token is null");
        }
        if (!serverConfig.getServerConfigToken().equals(token)) {
            throw new MsApiException(StatusCode.Base.BASE_PARA_ERROR, "server token is invalid!");
        }

    }

    @PostMapping("/server/get")
    @ResponseBody
    public ServerConfigResp getServerConfigResp(@Valid @RequestBody ServerConfigReq serverConfigReq) throws MsApiException {
        checkServerToken(serverConfigReq.getServerTokenFromMember());
        return new ServerConfigResp(serverConfig);
    }

    @PostMapping("/server/update")
    public ResponseEntity<String> updateServerConfig(@Valid @RequestBody ServerConfigReq serverConfigReq) throws MsApiException, IllegalAccessException {
        checkServerToken(serverConfigReq.getServerTokenFromMember());
        ServerConfig updateServerConfig = serverConfigReq.getServerConfig();
        serverConfigService.uploadAndLoadServerConfigToRedis(updateServerConfig);
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/server/reload")
    public ResponseEntity<String> reloadServerConfig(@Valid @RequestBody ServerConfigReq serverConfigReq) throws MsApiException, IllegalAccessException {
        checkServerToken(serverConfigReq.getServerTokenFromMember());
        serverConfigService.loadServerConfigFromRedis();
        return ResponseEntity.ok().body("ok");
    }

}
