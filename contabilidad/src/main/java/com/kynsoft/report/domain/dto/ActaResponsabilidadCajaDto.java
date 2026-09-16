package com.kynsoft.report.domain.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ActaResponsabilidadCajaDto { private UUID id; private Long numero; private UUID fincaId; private UUID custodioId; private String custodioNombre; private UUID usuarioEmisorId; private LocalDateTime fechaInicio; private LocalDateTime fechaCierre; private EstadoActaResponsabilidadCaja estado; private String observaciones; }
