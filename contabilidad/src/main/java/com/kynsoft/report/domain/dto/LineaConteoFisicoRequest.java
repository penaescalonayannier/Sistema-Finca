package com.kynsoft.report.domain.dto;
import lombok.Getter; import lombok.Setter; import java.util.UUID;
@Getter @Setter public class LineaConteoFisicoRequest { private UUID lineaId; private Double cantidadContada; private String observaciones; }
