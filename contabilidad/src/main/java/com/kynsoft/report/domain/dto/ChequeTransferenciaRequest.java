package com.kynsoft.report.domain.dto;
import lombok.Getter; import lombok.Setter; import java.time.LocalDateTime; import java.util.UUID;
@Getter @Setter public class ChequeTransferenciaRequest { private UUID fincaId; private TipoChequeTransferencia tipo; private LocalDateTime fechaEmision; private Double importe; private String beneficiario; private String concepto; private String referenciaBancaria; private String autorizadoPor; private String emitidoPor; private String observaciones; }
