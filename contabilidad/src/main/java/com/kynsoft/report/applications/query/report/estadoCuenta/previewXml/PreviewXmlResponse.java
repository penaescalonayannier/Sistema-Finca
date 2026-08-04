package com.kynsoft.report.applications.query.report.estadoCuenta.previewXml;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.EstadoCuentaDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreviewXmlResponse implements IResponse {

    private List<EstadoCuentaDto> operaciones;
    private int totalOperaciones;
    private Double saldoCreditoTotal;
    private Double saldoDebitoTotal;

    public PreviewXmlResponse(List<EstadoCuentaDto> operaciones) {
        this.operaciones = operaciones;
        this.totalOperaciones = operaciones.size();

        this.saldoCreditoTotal = operaciones.stream()
            .filter(op -> "Cr".equalsIgnoreCase(op.getTipo()))
            .mapToDouble(op -> op.getImporte() != null ? op.getImporte() : 0.0)
            .sum();

        this.saldoDebitoTotal = operaciones.stream()
            .filter(op -> "Db".equalsIgnoreCase(op.getTipo()))
            .mapToDouble(op -> op.getImporte() != null ? op.getImporte() : 0.0)
            .sum();
    }
}
