package com.kynsoft.report.domain.dto;
import lombok.Builder; import lombok.Getter; import java.time.*; import java.util.*;
@Getter @Builder public class ConciliacionBancariaDetalleResponse { private UUID id; private Long numero; private UUID fincaId; private LocalDate periodo; private LocalDateTime fecha; private Double saldoExtracto; private Double saldoLibros; private Double diferencia; private EstadoDocumentoControl estado; private String responsable; private String observaciones; private List<MovimientoConciliacionDto> movimientos; }
