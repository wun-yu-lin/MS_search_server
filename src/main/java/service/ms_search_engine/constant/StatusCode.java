package service.ms_search_engine.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class StatusCode {

    public interface  IStatusCode {}

    @AllArgsConstructor
    @Getter
    public enum Base implements IStatusCode{
        BASE_STATUS_ERROR("E9999"),

        BASE_PARA_ERROR("E9901"),

        BASE_DB_ERROR("E9902"),

        BASE_REDIS_ERROR("E9903"),

        BASE_S3_ERROR("E9904");

        @JsonValue
        private final String status;
    }
}
