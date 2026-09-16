package com.kynsoft.report.domain.dto;

import lombok.*;
import java.util.UUID;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class TableroControlCajaDto { private UUID fincaId; private Integer anio; private Integer mes; private Long arqueosDelMes; private Long arqueosCerrados; private Long arqueosAbiertos; private Boolean cumplimientoArqueoMensual; private Double saldoPorDepositar; private Boolean depositoPendiente; private Double fondoCambioAutorizado; private Double fondoPagosMenoresAutorizado; private Double fondoNominaAutorizado; private Long incidenciasPendientes; }
