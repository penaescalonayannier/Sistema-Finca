package com.kynsoft.report.applications.command.tomaprestamo.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.domain.dto.enumerativos.TipoTomaPrestamo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateTomaPrestamoCommand implements ICommand {
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

    public CreateTomaPrestamoCommand(Double importe, LocalDate fecha, String cuentaDestino, 
                                   TipoTomaPrestamo tipo, String observaciones, String creditoId,
                                   Double importeUtilizadoEfectivo, Double importeUtilizadoSuministros,
                                   Double importeUtilizadoSeguro) {
        this.id = UUID.randomUUID();
        this.importe = importe;
        this.fecha = fecha;
        this.cuentaDestino = cuentaDestino;
        this.tipo = tipo;
        this.observaciones = observaciones;
        this.creditoId = creditoId;
        this.importeUtilizadoEfectivo = importeUtilizadoEfectivo;
        this.importeUtilizadoSuministros = importeUtilizadoSuministros;
        this.importeUtilizadoSeguro = importeUtilizadoSeguro;
    }

    public static CreateTomaPrestamoCommand fromRequest(CreateTomaPrestamoRequest request) {
        return new CreateTomaPrestamoCommand(
                request.getImporte(),
                request.getFecha(),
                request.getCuentaDestino(),
                request.getTipo(),
                request.getObservaciones(),
                request.getCreditoId(),
                request.getImporteUtilizadoEfectivo(),
                request.getImporteUtilizadoSuministros(),
                request.getImporteUtilizadoSeguro()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateTomaPrestamoMessage(id);
    }
}