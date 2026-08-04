package com.kynsoft.report.applications.query.responseObject;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.TrabajadorReporteDetailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrabajadorReporteDetailResponse implements IResponse {
    private UUID id;
    private UUID trabajadorId;
    private String trabajadorNombre;
    private String trabajadorRuc;
    private String trabajadorCuenta;
    private String trabajadorCargo;
    private UUID reporteId;
    private String reporteCodigo;
    private String reporteBloque;
    private String reporteCampo;
    private String reporteArea;
    private String reporteNorma;
    private String reporteFecha;
    private String reporteYear;
    private String reporteMes;
    private String norma;
    private String horas;

    public TrabajadorReporteDetailResponse(TrabajadorReporteDetailDto dto) {
        this.id = dto.getId();
        this.trabajadorId = dto.getTrabajadorId();
        this.trabajadorNombre = dto.getTrabajadorNombre();
        this.trabajadorRuc = dto.getTrabajadorRuc();
        this.trabajadorCuenta = dto.getTrabajadorCuenta();
        this.trabajadorCargo = dto.getTrabajadorCargo();
        this.reporteId = dto.getReporteId();
        this.reporteCodigo = dto.getReporteCodigo();
        this.reporteBloque = dto.getReporteBloque();
        this.reporteCampo = dto.getReporteCampo();
        this.reporteArea = dto.getReporteArea();
        this.reporteNorma = dto.getReporteNorma();
        this.reporteFecha = dto.getReporteFecha();
        this.reporteYear = dto.getReporteYear();
        this.reporteMes = dto.getReporteMes();
        this.norma = dto.getNorma();
        this.horas = dto.getHoras();
    }
}