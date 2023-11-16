package service.ms_search_engine.exception;

import org.springframework.http.ResponseEntity;

public class S3DataUploadException extends Exception {
    public S3DataUploadException(String message) {
        super(message);
    }
}
