package com.kynsoft.share.core.domain.exception;

import com.kynsoft.share.core.domain.response.ErrorField;
import lombok.Getter;

@Getter
public class InvalidOtpException extends RuntimeException {
    private final ErrorField errorField;
    private final int status;

    public InvalidOtpException(String message, ErrorField errorField) {
        super(message);
        this.errorField = errorField;
        this.status = 901;
    }
}
