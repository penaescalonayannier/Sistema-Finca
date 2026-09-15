package com.kynsoft.report.applications.command.salida.update;

import com.kynsoft.report.domain.dto.ItemSalidaDto;
import com.kynsoft.report.domain.dto.TipoSalida;
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
public class UpdateSalidaRequest {
    private UUID id;
    private TipoSalida tipo;
    private UUID fincaProductoId;
    private String observaciones;
    private List<ItemSalidaDto> items;
}
