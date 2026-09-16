package com.kynsoft.report.infrastructure.entity;
import com.kynsoft.report.domain.dto.EstadoActaResponsabilidadCaja;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter @Setter @Entity @Table(name = "acta_responsabilidad_caja")
public class ActaResponsabilidadCaja { @Id private UUID id; @Column(nullable=false, unique=true) private Long numero; @Column(name="finca_id", nullable=false) private UUID fincaId; @Column(name="custodio_id") private UUID custodioId; @Column(name="custodio_nombre", nullable=false) private String custodioNombre; @Column(name="usuario_emisor_id") private UUID usuarioEmisorId; @Column(name="fecha_inicio", nullable=false) private LocalDateTime fechaInicio; @Column(name="fecha_cierre") private LocalDateTime fechaCierre; @Enumerated(EnumType.STRING) @Column(nullable=false) private EstadoActaResponsabilidadCaja estado; private String observaciones; }
