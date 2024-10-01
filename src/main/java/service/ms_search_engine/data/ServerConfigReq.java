package service.ms_search_engine.data;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import service.ms_search_engine.config.ServerConfig;

@Data
public class ServerConfigReq {
    private ServerConfig serverConfig;

    @NotBlank
    private String serverTokenFromMember;
}
