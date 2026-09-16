package com.kynsoft.report.infrastructure.entity;
import com.kynsoft.report.domain.dto.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter @Setter @Entity @Table(name = "incidencia_arqueo_caja")
public class IncidenciaArqueoCaja { @Id private UUID id; @Column(name="arqueo_caja_id", nullable=false, unique=true) private UUID arqueoCajaId; @Column(name="finca_id", nullable=false) private UUID fincaId; @Enumerated(EnumType.STRING) @Column(nullable=false) private TipoIncidenciaArqueoCaja tipo; @Column(nullable=false) private Double importe; @Column(nullable=false) private String expediente; @Column(nullable=false) private String descripcion; @Enumerated(EnumType.STRING) @Column(nullable=false) private EstadoIncidenciaArqueoCaja estado; @Column(name="creado_por_id") private UUID creadoPorId; @Column(name="aprobado_por_id") private UUID aprobadoPorId; @Column(name="fecha_creacion", nullable=false) private LocalDateTime fechaCreacion; @Column(name="fecha_resolucion") private LocalDateTime fechaResolucion; private String observaciones; }
