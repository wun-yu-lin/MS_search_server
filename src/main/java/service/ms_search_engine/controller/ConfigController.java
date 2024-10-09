package service.ms_search_engine.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ms_search_engine.annotation.MSUserAuth;
import service.ms_search_engine.config.ServerConfig;
import service.ms_search_engine.constant.MSConstant;
import service.ms_search_engine.data.ServerConfigReq;
import service.ms_search_engine.data.ServerConfigResp;
import service.ms_search_engine.exception.MsApiException;
import service.ms_search_engine.service.ServerConfigService;

@RestController
@RequestMapping("/api/config")
public class ConfigController extends BaseController {


    @Autowired
    private ServerConfigService serverConfigService;

    @PostMapping("/server/get")
    @ResponseBody
    @MSUserAuth(userRoles = {MSConstant.USER_ROLE.ADMIN}, checkServerToken = true)
    public ServerConfigResp getServerConfigResp(@Valid @RequestBody ServerConfigReq serverConfigReq) throws MsApiException {
        return new ServerConfigResp(serverConfig);
    }

    @PostMapping("/server/update")
    @MSUserAuth(userRoles = {MSConstant.USER_ROLE.ADMIN}, checkServerToken = true)
    public ResponseEntity<String> updateServerConfig(@Valid @RequestBody ServerConfigReq serverConfigReq) throws MsApiException, IllegalAccessException {
        ServerConfig updateServerConfig = serverConfigReq.getServerConfig();
        serverConfigService.uploadAndLoadServerConfigToRedis(updateServerConfig);
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/server/reload")
    @MSUserAuth(userRoles = {MSConstant.USER_ROLE.ADMIN}, checkServerToken = true)
    public ResponseEntity<String> reloadServerConfig(@Valid @RequestBody ServerConfigReq serverConfigReq) throws MsApiException, IllegalAccessException {
        serverConfigService.loadServerConfigFromRedis();
        return ResponseEntity.ok().body("ok");
    }

}
