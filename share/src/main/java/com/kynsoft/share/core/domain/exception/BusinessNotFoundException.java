package com.kynsoft.share.core.domain.exception;

import lombok.Getter;

public class BusinessNotFoundException extends RuntimeException {

    @Getter
    private final GlobalBusinessException brokenRule;

    @Getter
    private final int status;

    private final String message;

    public BusinessNotFoundException(GlobalBusinessException brokenRule) {
        super(brokenRule.getError().getReasonPhrase());
        this.status = brokenRule.getError().value();
        this.message = brokenRule.getError().getReasonPhrase();
        this.brokenRule = brokenRule;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
