package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.PrestamoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class PrestamoResponse implements IResponse {

    private UUID id;
    private Double importeAprobado;
    private Double importeAprobadoEfectivo;
    private Double importeUtilizadoEfectivo;
    private Double importeAprobadoSuministros;
    private Double importeUtilizadoSuministros;
    private Double importeAprobadoSeguro;
    private Double importeUtilizadoSeguro;
    private String numeroContrato;
    private String cuenta;
    private String observaciones;
    private String toneladasMolibles;

    public PrestamoResponse(PrestamoDto prestamo) {
        this.id = prestamo.getId();
        this.importeAprobado = prestamo.getImporteAprobado();
        this.importeAprobadoEfectivo = prestamo.getImporteAprobadoEfectivo();
        this.importeUtilizadoEfectivo = prestamo.getImporteUtilizadoEfectivo();
        this.importeAprobadoSuministros = prestamo.getImporteAprobadoSuministros();
        this.importeUtilizadoSuministros = prestamo.getImporteUtilizadoSuministros();
        this.importeAprobadoSeguro = prestamo.getImporteAprobadoSeguro();
        this.importeUtilizadoSeguro = prestamo.getImporteUtilizadoSeguro();
        this.numeroContrato = prestamo.getNumeroContrato();
        this.cuenta = prestamo.getCuenta();
        this.observaciones = prestamo.getObservaciones();
        this.toneladasMolibles = prestamo.getToneladasMolibles();
    }
}
