package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import lombok.Builder;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class ClienteDto {

    private UUID id;
    private String cuenta;
    private String nombre;
    private String ruc;
    private String direccion;
}
