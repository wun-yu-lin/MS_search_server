package service.ms_search_engine.exception;


import service.ms_search_engine.constant.StatusCode;

public class S3DataUploadException extends MsApiException {
    public S3DataUploadException(String message) {
        super(StatusCode.Base.BASE_REDIS_ERROR, message);
    }
}
