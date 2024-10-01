package service.ms_search_engine.exception;

import service.ms_search_engine.constant.StatusCode;

public class DatabaseDeleteErrorException extends MsApiException {
    public DatabaseDeleteErrorException(String message){
        super(StatusCode.Base.BASE_DB_ERROR, message);
    }
}
