package service.ms_search_engine.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class MSControllerExceptionAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> prepareResponseForRunTimeException(RuntimeException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("RunTimeException: " + exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> prepareResponseForIOException(IOException exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("RunTimeException: " + exception.getMessage());
    }

    @ExceptionHandler(QueryParameterException.class)
    public ResponseEntity<String> prepareResponseForQueryParameterException(QueryParameterException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("QueryParameterException: " + exception.getMessage());
    }

    @ExceptionHandler(DatabaseInsertErrorException.class)
    public ResponseEntity<String> prepareResponseForDatabaseInsertErrorException(DatabaseInsertErrorException exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("DatabaseInsertErrorException: " + exception.getMessage());
    }

    @ExceptionHandler(DatabaseDeleteErrorException.class)
    public ResponseEntity<String> prepareResponseForDatabaseDeleteErrorException(DatabaseDeleteErrorException exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("DatabaseDeleteErrorException: " + exception.getMessage());
    }

    @ExceptionHandler(S3DataUploadException.class)
    public ResponseEntity<String> prepareResponseForS3DataUploadException(S3DataUploadException exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("S3DataUploadException: " + exception.getMessage());
    }
    @ExceptionHandler(S3DataDeleteException.class)
    public ResponseEntity<String> prepareResponseForS3DataDeleteException(S3DataDeleteException exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("S3DataDeleteException: " + exception.getMessage());
    }

    @ExceptionHandler(RedisErrorException.class)
    public ResponseEntity<String> prepareResponseForRedisQueueErrorException(RedisErrorException exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("RedisQueueErrorException: " + exception.getMessage());
    }

    @ExceptionHandler(S3DataDownloadException.class)
    public ResponseEntity<String> prepareResponseForS3DataDownloadException(S3DataDownloadException exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("S3DataDownloadException: " + exception.getMessage());
    }

    @ExceptionHandler(MsApiException.class)
    public ResponseEntity<String> prepareResponseForMsApiException(MsApiException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MsApiException.class + exception.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> prepareResponseForException(Exception exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("BaseException: " + exception.getMessage());
    }
}
