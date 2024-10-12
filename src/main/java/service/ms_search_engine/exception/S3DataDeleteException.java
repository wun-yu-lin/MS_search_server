package service.ms_search_engine.exception;

import service.ms_search_engine.constant.StatusCode;

public class S3DataDeleteException extends MsApiException{
    public S3DataDeleteException(String message){
        super(StatusCode.Base.BASE_S3_ERROR, message);
    }
}
