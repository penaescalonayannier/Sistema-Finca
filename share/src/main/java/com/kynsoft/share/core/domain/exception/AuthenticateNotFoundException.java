package com.kynsoft.share.core.domain.exception;


import com.kynsoft.share.core.domain.response.ErrorField;
import lombok.Getter;

@Getter
public class AuthenticateNotFoundException extends RuntimeException {
    private final ErrorField errorField;

    public AuthenticateNotFoundException(String message, ErrorField errorField) {
        super(message);
        this.errorField = errorField;
    }

}
