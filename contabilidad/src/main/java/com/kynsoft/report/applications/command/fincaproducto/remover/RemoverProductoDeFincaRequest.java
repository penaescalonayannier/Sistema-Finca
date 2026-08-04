package com.kynsoft.report.applications.command.fincaproducto.remover;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RemoverProductoDeFincaRequest {
    private UUID fincaId;
    private UUID productoId;
}