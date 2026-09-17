package com.kynsoft.report.domain.dto;
import lombok.Builder; import lombok.Getter; import java.util.UUID;
@Getter @Builder public class ConteoFisicoLineaDto { private UUID id; private UUID almacenFincaProductoId; private String productoCodigo; private String productoNombre; private String unidadMedida; private Double existenciaTeorica; private Double existenciaFisica; private Double diferencia; private String observaciones; }
