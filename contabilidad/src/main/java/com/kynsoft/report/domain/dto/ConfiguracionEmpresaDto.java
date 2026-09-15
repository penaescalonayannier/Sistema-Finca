package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para configuración de empresa.
 * Campos obligatorios según modelos oficiales cubanos SC-2-08 y SC-2-12.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionEmpresaDto {

    private UUID id;

    private String nombre;

    private String codigo;

    private String nit;

    private String direccion;

    private String municipio;

    private String provincia;

    private String cuentaBancaria;

    private String banco;

    private String telefono;

    private String email;

    private Boolean activo;

    /**
     * Dirección completa formateada para documentos.
     */
    public String getDireccionCompleta() {
        StringBuilder sb = new StringBuilder();
        if (direccion != null && !direccion.isEmpty()) {
            sb.append(direccion);
        }
        if (municipio != null && !municipio.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(municipio);
        }
        if (provincia != null && !provincia.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(provincia);
        }
        return sb.toString();
    }
}
