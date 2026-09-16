package com.kynsoft.report.domain.dto;
import lombok.Builder; import lombok.Getter; import java.time.LocalDateTime; import java.util.UUID;
@Getter @Builder public class DocumentoCajaResponse { private UUID id; private Long numero; private UUID fincaId; private TipoDocumentoCaja tipo; private LocalDateTime fecha; private Double importe; private String beneficiario; private String concepto; private String entregadoPor; private String recibidoPor; private String autorizadoPor; private String referencia; private String observaciones; private EstadoDocumentoControl estado; }
