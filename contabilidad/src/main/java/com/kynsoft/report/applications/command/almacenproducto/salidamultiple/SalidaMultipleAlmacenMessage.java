package com.kynsoft.report.applications.command.almacenproducto.salidamultiple;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SalidaMultipleAlmacenMessage implements ICommandMessage {
    private List<UUID> salidaIds;
    private Integer cantidadLineas;
    private final String command = "SALIDA_MULTIPLE_ALMACEN";
}
