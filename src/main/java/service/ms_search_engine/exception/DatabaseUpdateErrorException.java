package service.ms_search_engine.exception;

import service.ms_search_engine.constant.StatusCode;

public class DatabaseUpdateErrorException extends MsApiException{
    public DatabaseUpdateErrorException(String message){
        super(StatusCode.Base.BASE_DB_ERROR, message);
    }
}
