package service.ms_search_engine.data.base;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

@Data
public class BaseReqBody {

    @NotBlank
    private String ipAddress = StringUtils.EMPTY;

    private String timeZone = StringUtils.EMPTY;

    private String requestId = UUID.randomUUID().toString();
}
