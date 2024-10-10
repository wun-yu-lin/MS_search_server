package service.ms_search_engine.exception;

import service.ms_search_engine.constant.StatusCode;

public class QueryParameterException extends MsApiException{
    public QueryParameterException(String message){
        super(StatusCode.Base.BASE_PARA_ERROR, message);
    }

}
