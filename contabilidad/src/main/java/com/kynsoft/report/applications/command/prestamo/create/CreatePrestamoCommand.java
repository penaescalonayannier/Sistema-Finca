package com.kynsoft.report.applications.command.prestamo.create;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePrestamoCommand implements ICommand {

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

    public CreatePrestamoCommand(Double importeAprobado, Double importeAprobadoEfectivo,
                                  Double importeUtilizadoEfectivo, Double importeAprobadoSuministros,
                                  Double importeUtilizadoSuministros, Double importeAprobadoSeguro,
                                  Double importeUtilizadoSeguro, String numeroContrato,
                                  String cuenta, String observaciones, String toneladasMolibles) {
        this.id = UUID.randomUUID();
        this.importeAprobado = importeAprobado;
        this.importeAprobadoEfectivo = importeAprobadoEfectivo;
        this.importeUtilizadoEfectivo = importeUtilizadoEfectivo;
        this.importeAprobadoSuministros = importeAprobadoSuministros;
        this.importeUtilizadoSuministros = importeUtilizadoSuministros;
        this.importeAprobadoSeguro = importeAprobadoSeguro;
        this.importeUtilizadoSeguro = importeUtilizadoSeguro;
        this.numeroContrato = numeroContrato;
        this.cuenta = cuenta;
        this.observaciones = observaciones;
        this.toneladasMolibles = toneladasMolibles;
    }

    public static CreatePrestamoCommand fromRequest(CreatePrestamoRequest request) {
        return new CreatePrestamoCommand(
                request.getImporteAprobado(),
                request.getImporteAprobadoEfectivo(),
                request.getImporteUtilizadoEfectivo(),
                request.getImporteAprobadoSuministros(),
                request.getImporteUtilizadoSuministros(),
                request.getImporteAprobadoSeguro(),
                request.getImporteUtilizadoSeguro(),
                request.getNumeroContrato(),
                request.getCuenta(),
                request.getObservaciones(),
                request.getToneladasMolibles()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePrestamoMessage(id);
    }
}
