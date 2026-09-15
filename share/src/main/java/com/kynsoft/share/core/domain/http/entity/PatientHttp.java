package com.kynsoft.share.core.domain.http.entity;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PatientHttp implements IResponse, Serializable {

    private UUID id;
    private String identification;
    private String name;
    private String lastName;
    private String gender;
    private String status;
    private String birthDate;
    private String email;
    private String phone;
    private String profession;
    private String skinColor;
}
