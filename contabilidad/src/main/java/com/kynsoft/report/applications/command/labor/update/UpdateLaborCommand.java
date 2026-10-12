package com.kynsoft.report.applications.command.labor.update;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class UpdateLaborCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;
    private Boolean activo;
    private UUID tipoCultivoId;
    private String codigoCentroCosto;
    private String unidadMedida;
    private BigDecimal normaRendimiento;
    private BigDecimal indiceConsumo;
    private String nivelActividad;
    private BigDecimal dotacionEstandar;
    private BigDecimal diasEstimados;
    private BigDecimal horasEstimadas;
    private Set<UUID> cargosRequeridos;

    public static UpdateLaborCommand fromRequest(UpdateLaborRequest request, UUID id) {
        return new UpdateLaborCommand(
                id,
                request.getCode(),
                request.getName(), request.getActivo(), request.getTipoCultivoId(), request.getCodigoCentroCosto(),
                request.getUnidadMedida(), request.getNormaRendimiento(), request.getIndiceConsumo(), request.getNivelActividad(), request.getDotacionEstandar(),
                request.getDiasEstimados(), request.getHorasEstimadas(), request.getCargosRequeridos()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateLaborMessage(id);
    }
}
