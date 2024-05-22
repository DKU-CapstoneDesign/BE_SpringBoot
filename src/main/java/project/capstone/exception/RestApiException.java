package project.capstone.exception;

import lombok.Getter;
import project.capstone.entity.enumSet.ErrorType;

@Getter
public class RestApiException extends RuntimeException {
    private final ErrorType errorType;

    public RestApiException(ErrorType errorType) {
        this.errorType = errorType;
    }
}