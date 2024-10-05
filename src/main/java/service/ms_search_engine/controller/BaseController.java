package service.ms_search_engine.controller;


import org.springframework.beans.factory.annotation.Autowired;
import service.ms_search_engine.config.ServerConfig;

public class BaseController {

    @Autowired
    protected ServerConfig serverConfig;
}
