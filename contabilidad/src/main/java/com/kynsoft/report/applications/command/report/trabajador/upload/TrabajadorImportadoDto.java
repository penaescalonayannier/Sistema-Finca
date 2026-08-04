package com.kynsoft.report.applications.command.report.trabajador.upload;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class TrabajadorImportadoDto {
    
    private final UUID idCliente; // ID de la entidad Cliente creada
    private final String ci;
    private final String nombre;
    private final String cuentaE;
}