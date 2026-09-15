package com.kynsoft.share.core.domain.exception;

import lombok.Getter;

public class DoctorIsNotPermisionNotFoundException extends RuntimeException {

    @Getter
    private final GlobalBusinessException brokenRule;

    @Getter
    private final int status;

    private final String message;

    public DoctorIsNotPermisionNotFoundException(GlobalBusinessException brokenRule) {
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
