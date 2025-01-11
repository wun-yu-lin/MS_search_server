package service.ms_search_engine.data.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minidev.json.annotate.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

@EqualsAndHashCode(callSuper = true)
@Data
public class RestRespBody<E extends BaseRespData> extends BaseRespBody {

    @JsonIgnore
    private static final String success = "0000";

    @JsonIgnore
    private static final DefaultRespData defaultData = new DefaultRespData();

    private String apiStatus = success;

    private String desc = StringUtils.EMPTY;

    private E data;


    public static RestRespBody<DefaultRespData> success () {
        RestRespBody<DefaultRespData> resp = new RestRespBody<>();
        DefaultRespData data = new DefaultRespData();
        data = data.getClass().getDeclaredFields().length == 0 ? null : data;
        resp.setData(data);
        return resp;
    }

}
