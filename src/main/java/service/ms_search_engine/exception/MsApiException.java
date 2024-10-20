package service.ms_search_engine.exception;

import lombok.Getter;
import service.ms_search_engine.constant.StatusCode;

public class MsApiException extends RuntimeException {

    @Getter
    private final Enum<?> status;

    public MsApiException(Enum<?> statusCode, String message, Throwable e) {
        super(buildErrorMessage(statusCode.name(), message), e);
        this.status = statusCode;
    }

    public MsApiException(Enum<?> statusCode, Throwable e) {
        super(buildErrorMessage(statusCode.name()), e);
        this.status = statusCode;
    }

    public MsApiException(Enum<?> statusCode, String message) {
        super(buildErrorMessage(statusCode.name(), message));
        this.status = statusCode;
    }
    private static String buildErrorMessage(String statusCode) {
        return buildErrorMessage(statusCode, "error");
    }
    private static String buildErrorMessage(String statusCode, String message) {
        StringBuffer sb = new StringBuffer();
        sb.append("Error status: ");
        sb.append(statusCode);
        sb.append("\n"); // 正確的換行符號
        sb.append("Error message: ");
        sb.append(message);
        return sb.toString();
    }
}
