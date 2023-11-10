package service.ms_search_engine.exception;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseInsertErrorException extends SQLException {
    public DatabaseInsertErrorException(String message){
        super(message);
    }
}
