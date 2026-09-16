package com.kynsoft.report.domain.dto;

import lombok.*;
import java.util.UUID;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CrearIncidenciaArqueoCajaRequest { private UUID arqueoCajaId; private String expediente; private String descripcion; }
