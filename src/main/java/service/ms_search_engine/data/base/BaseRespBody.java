package service.ms_search_engine.data.base;

import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

@Data
public class BaseRespBody {

    @JsonIgnore
    private static final String success = "0000";

    @JsonIgnore static final String successDesc = "success";

    private String apiStatus = success;

    private String desc = StringUtils.EMPTY;
}
