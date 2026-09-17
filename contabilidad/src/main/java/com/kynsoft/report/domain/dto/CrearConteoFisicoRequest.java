package com.kynsoft.report.domain.dto;
import lombok.Getter; import lombok.Setter; import java.util.UUID;
@Getter @Setter public class CrearConteoFisicoRequest { private UUID almacenId; private String responsableConteo; private String verificadoPor; private String observaciones; }
