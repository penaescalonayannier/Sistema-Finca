package com.kynsoft.share.core.domain.exception;


import lombok.Getter;

public class BusinessException extends RuntimeException {

    @Getter
    private final int status;

    private final String message;

    @Getter
    private final String details;

    public BusinessException(DomainErrorMessage status, String details) {
        super(status.getReasonPhrase());
        this.status = status.value();
        this.message = status.getReasonPhrase();
        this.details = details;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
