package com.kynsoft.report.domain.dto;

import lombok.*;
import java.util.UUID;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FondoCajaRequest { private UUID fincaId; private TipoFondoCaja tipo; private Double importeAutorizado; private String observaciones; private Boolean activo; }
