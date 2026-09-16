package com.kynsoft.report.domain.dto;

import lombok.*;
import java.util.UUID;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ActaResponsabilidadCajaRequest { private UUID fincaId; private UUID custodioId; private String custodioNombre; private String observaciones; }
