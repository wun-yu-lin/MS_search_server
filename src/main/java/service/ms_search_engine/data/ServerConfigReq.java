package service.ms_search_engine.data;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import service.ms_search_engine.config.ServerConfig;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServerConfigReq extends BaseAuthRequest {
    private ServerConfig serverConfig;


}
