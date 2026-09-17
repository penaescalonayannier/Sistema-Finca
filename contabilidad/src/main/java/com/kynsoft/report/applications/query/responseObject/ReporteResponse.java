package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.ReporteDto;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ReporteResponse implements IResponse {

    private UUID id;
    private String bloque;
    private String campo;
    private String area;
    private String norma;
    private String fecha;
    private String codigo;
    private String year;
    private String mes;
    private UUID trabajadorResponsableId;
    private String trabajadorResponsableNombre;
    private UUID tipoReporteId;
    private String tipoReporteCodigo;
    private String tipoReporteNombre;
    private UUID tipoCultivoId;
    private String tipoCultivoNombre;
    private UUID tipoAnimalId;
    private String tipoAnimalNombre;

    public ReporteResponse(ReporteDto dto) {
        this.id = dto.getId();
        this.bloque = dto.getBloque();
        this.campo = dto.getCampo();
        this.area = dto.getArea();
        this.norma = dto.getNorma();
        this.fecha = dto.getFecha();
        this.codigo = dto.getCodigo();
        this.year = dto.getYear();
        this.mes = dto.getMes();
        this.trabajadorResponsableId = dto.getTrabajadorResponsableId();
        this.trabajadorResponsableNombre = dto.getTrabajadorResponsableNombre();
        this.tipoReporteId = dto.getTipoReporteId();
        this.tipoReporteCodigo = dto.getTipoReporteCodigo();
        this.tipoReporteNombre = dto.getTipoReporteNombre();
        this.tipoCultivoId = dto.getTipoCultivoId();
        this.tipoCultivoNombre = dto.getTipoCultivoNombre();
        this.tipoAnimalId = dto.getTipoAnimalId();
        this.tipoAnimalNombre = dto.getTipoAnimalNombre();
    }

}
