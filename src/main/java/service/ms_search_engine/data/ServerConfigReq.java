package service.ms_search_engine.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import service.ms_search_engine.config.ServerConfig;
import service.ms_search_engine.data.base.BaseAuthRequestData;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServerConfigReq extends BaseAuthRequestData {
    private ServerConfig serverConfig;


}
