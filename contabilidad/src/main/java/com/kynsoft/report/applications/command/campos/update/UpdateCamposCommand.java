package com.kynsoft.report.applications.command.campos.update;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class UpdateCamposCommand implements ICommand {
    private UUID id;
    private UUID bloque;
    private UUID cepa;
    private UUID variedad;
    private String campo;
    private Double area;
    private Double poblacion;
    private String destino;
    private Double rendimiento;
    private Double valorAdquisicion;
    private Double depreciacionAcumulada;
    private Double valorResidual;
    private Integer anosCepa;
    private Double tasaDepreciacionAnual;
    private Integer vidaUtilAnios;
    private LocalDate fechaInicioDepreciacion;

    public static UpdateCamposCommand fromRequest(UpdateCamposRequest request, UUID id) {
        return new UpdateCamposCommand(
                id,
                request.getBloque(),
                request.getCepa(),
                request.getVariedad(),
                request.getCampo(),
                request.getArea(),
                request.getPoblacion(),
                request.getDestino(),
                request.getRendimiento(),
                request.getValorAdquisicion(),
                request.getDepreciacionAcumulada(),
                request.getValorResidual(),
                request.getAnosCepa(),
                request.getTasaDepreciacionAnual(),
                request.getVidaUtilAnios(),
                request.getFechaInicioDepreciacion()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateCamposMessage(id);
    }
}