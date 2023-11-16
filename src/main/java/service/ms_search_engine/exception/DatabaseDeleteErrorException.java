package service.ms_search_engine.exception;

import java.sql.SQLException;

public class DatabaseDeleteErrorException extends SQLException {
    public DatabaseDeleteErrorException(String message){
        super(message);
    }
}
