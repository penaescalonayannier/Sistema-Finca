package com.kynsoft.share.core.application.payment.domain.placeToPlay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentServiceStatusResponse {
    private String reference;
    private String authorization;
    private String status;
    private String reason;
    private String message;
    private String date;
}
