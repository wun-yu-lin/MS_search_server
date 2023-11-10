package service.ms_search_engine.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class ControllerException {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> prepareResponseForRunTimeException(RuntimeException exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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
}
