package service.ms_search_engine.data;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import service.ms_search_engine.config.ServerConfig;

@Data
@AllArgsConstructor
public class ServerConfigResp {

    private ServerConfig serverConfig;
}
