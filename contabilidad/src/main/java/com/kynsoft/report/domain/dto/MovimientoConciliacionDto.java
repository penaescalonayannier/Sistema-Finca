package com.kynsoft.report.domain.dto;
import lombok.Getter; import lombok.Setter; import java.time.LocalDate;
@Getter @Setter public class MovimientoConciliacionDto { private LocalDate fecha; private String origen; private String descripcion; private String referencia; private Double importe; private Boolean conciliado; private String observaciones; }
