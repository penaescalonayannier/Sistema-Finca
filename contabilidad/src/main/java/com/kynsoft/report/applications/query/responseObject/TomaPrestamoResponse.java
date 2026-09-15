package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.TomaPrestamoDto;
import com.kynsoft.report.domain.dto.enumerativos.TipoTomaPrestamo;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import lombok.Builder;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class TomaPrestamoResponse implements IResponse {

    private UUID id;
    private Double importe;
    private LocalDate fecha;
    private String cuentaDestino;
    private TipoTomaPrestamo tipo;
    private String observaciones;
    private String creditoId;
    private Double importeUtilizadoEfectivo;
    private Double importeUtilizadoSuministros;
    private Double importeUtilizadoSeguro;

    public TomaPrestamoResponse(TomaPrestamoDto dto) {
        this.id = dto.getId();
        this.importe = dto.getImporte();
        this.fecha = dto.getFecha();
        this.cuentaDestino = dto.getCuentaDestino();
        this.tipo = dto.getTipo();
        this.observaciones = dto.getObservaciones();
        this.creditoId = dto.getCreditoId();
        this.importeUtilizadoEfectivo = dto.getImporteUtilizadoEfectivo();
        this.importeUtilizadoSuministros = dto.getImporteUtilizadoSuministros();
        this.importeUtilizadoSeguro = dto.getImporteUtilizadoSeguro();
    }

}
