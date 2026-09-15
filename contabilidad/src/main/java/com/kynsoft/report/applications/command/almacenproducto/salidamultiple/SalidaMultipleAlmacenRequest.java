package com.kynsoft.report.applications.command.almacenproducto.salidamultiple;

import com.kynsoft.report.domain.dto.DestinoSalida;
import com.kynsoft.report.domain.dto.LineaSalidaMultipleAlmacenDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalidaMultipleAlmacenRequest {
    private DestinoSalida destino;
    private String observaciones;
    private List<LineaSalidaMultipleAlmacenDto> lineas;
}
