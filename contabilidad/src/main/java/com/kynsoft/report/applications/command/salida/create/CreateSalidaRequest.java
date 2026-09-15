package com.kynsoft.report.applications.command.salida.create;

import com.kynsoft.report.domain.dto.DestinoSalida;
import com.kynsoft.report.domain.dto.ItemSalidaDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSalidaRequest {
    // RN-09: tipo se determina automáticamente según destino
    private DestinoSalida destino;
    private UUID fincaProductoId;
    private UUID almacenFincaProductoId; // Optional: when creating from almacen view
    private String observaciones;
    private List<ItemSalidaDto> items;
}
