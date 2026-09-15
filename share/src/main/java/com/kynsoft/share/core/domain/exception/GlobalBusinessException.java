package com.kynsoft.share.core.domain.exception;

import com.kynsoft.share.core.domain.response.ErrorField;
import lombok.Getter;

@Getter
public class GlobalBusinessException extends Throwable {

    private final DomainErrorMessage error;

    private final ErrorField errorField;

    public GlobalBusinessException(DomainErrorMessage error, ErrorField errorField) {
        this.error = error;
        this.errorField = errorField;
    }

}
