package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalidaPendienteLiquidacionDto {
    private UUID salidaId;
    private String numero;
    private TipoSalida tipo;
    private DestinoSalida destino;
    private LocalDateTime fecha;
    private String fincaNombre;
    private Double importeTotal;
    private Double importeCobrado;
    private Double saldoPendiente;
    private String estadoCobro;
    private List<ItemSalidaPendienteLiquidacionDto> items;
}
