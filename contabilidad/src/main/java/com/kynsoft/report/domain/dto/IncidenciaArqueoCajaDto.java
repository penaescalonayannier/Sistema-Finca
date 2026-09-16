package com.kynsoft.report.domain.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class IncidenciaArqueoCajaDto { private UUID id; private UUID arqueoCajaId; private UUID fincaId; private TipoIncidenciaArqueoCaja tipo; private Double importe; private String expediente; private String descripcion; private EstadoIncidenciaArqueoCaja estado; private UUID creadoPorId; private UUID aprobadoPorId; private LocalDateTime fechaCreacion; private LocalDateTime fechaResolucion; private String observaciones; }
