package com.kynsoft.report.applications.command.trabajador.transferir;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TransferirTrabajadorRequest {
    private UUID nuevaFincaId;
}
