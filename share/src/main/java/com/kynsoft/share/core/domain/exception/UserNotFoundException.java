package com.kynsoft.share.core.domain.exception;


import com.kynsoft.share.core.domain.response.ErrorField;
import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final ErrorField errorField;

    public UserNotFoundException(String message, ErrorField errorField) {
        super(message);
        this.errorField = errorField;
    }

}
