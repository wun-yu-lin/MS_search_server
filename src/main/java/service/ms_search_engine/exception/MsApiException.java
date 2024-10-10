package service.ms_search_engine.exception;

import service.ms_search_engine.constant.StatusCode;

public class MsApiException extends Exception {

    public MsApiException(Enum<?> statusCode, String message, Throwable e) {
        super(buildErrorMessage(statusCode.name(), message), e);
    }

    public MsApiException(Enum<?> statusCode, String message) {
        super(buildErrorMessage(statusCode.name(), message));
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
