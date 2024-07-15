package service.ms_search_engine.constant;

import lombok.Getter;


@Getter
public enum TaskStatus {
    NOT_SUBMIT(0, "Not submit"),
    SUBMIT_IN_WAITING(1, "Submit in waiting"),
    PROCESSING(2, "Processing"),
    FINISH(3, "Task finish"),
    ERROR(4, "Task error"),
    DELETE(5, "Delete")dwdw
    ;

    private final int statusCode;
    private final String description;

    TaskStatus(int statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;

    }

}
