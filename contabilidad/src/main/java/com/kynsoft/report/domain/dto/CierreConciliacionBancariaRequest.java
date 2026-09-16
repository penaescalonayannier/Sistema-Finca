package com.kynsoft.report.domain.dto;
import lombok.Getter; import lombok.Setter; import java.time.LocalDate; import java.util.List; import java.util.UUID;
@Getter @Setter public class CierreConciliacionBancariaRequest { private UUID fincaId; private LocalDate periodo; private Double saldoExtracto; private Double saldoLibros; private String responsable; private String observaciones; private List<MovimientoConciliacionDto> movimientos; }
