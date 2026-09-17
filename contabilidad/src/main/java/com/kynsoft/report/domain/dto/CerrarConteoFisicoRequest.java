package com.kynsoft.report.domain.dto;
import lombok.Getter; import lombok.Setter; import java.util.List;
@Getter @Setter public class CerrarConteoFisicoRequest { private List<LineaConteoFisicoRequest> lineas; private String observaciones; private String autorizadoPor; }
