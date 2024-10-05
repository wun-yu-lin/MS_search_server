package service.ms_search_engine.exception;

import service.ms_search_engine.constant.StatusCode;

public class S3DataDownloadException extends MsApiException{
    public S3DataDownloadException(String message) {
        super(StatusCode.Base.BASE_S3_ERROR, message);
    }
}
