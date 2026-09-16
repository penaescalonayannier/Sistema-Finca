package com.kynsoft.report.infrastructure.entity;
import com.kynsoft.report.domain.dto.TipoFondoCaja;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
@Getter @Setter @Entity @Table(name = "fondo_caja_autorizado")
public class FondoCajaAutorizado { @Id private UUID id; @Column(name="finca_id", nullable=false) private UUID fincaId; @Enumerated(EnumType.STRING) @Column(nullable=false) private TipoFondoCaja tipo; @Column(name="importe_autorizado", nullable=false) private Double importeAutorizado; private String observaciones; @Column(nullable=false) private Boolean activo = true; }
