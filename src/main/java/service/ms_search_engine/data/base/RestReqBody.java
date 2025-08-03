package service.ms_search_engine.data.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RestReqBody <E extends BaseRequestData> extends BaseReqBody {

    private E data;

}
