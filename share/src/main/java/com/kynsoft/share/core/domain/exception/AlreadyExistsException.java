package com.kynsoft.share.core.domain.exception;


import com.kynsoft.share.core.domain.response.ErrorField;
import lombok.Getter;

@Getter
public class AlreadyExistsException extends RuntimeException {
    private final ErrorField errorField;

    public AlreadyExistsException(String message, ErrorField errorField) {
        super(message);
        this.errorField = errorField;
    }

}
