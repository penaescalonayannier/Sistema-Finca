package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.DiaTrabajoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaTrabajoResponse implements IResponse {
    private UUID id;
    private LocalDate fecha;
    private UUID reporteId;
    private List<TrabajadorDiaResponse> trabajadores;

    public DiaTrabajoResponse(DiaTrabajoDto dto) {
        this.id = dto.getId();
        this.fecha = dto.getFecha();
        this.reporteId = dto.getReporteId();
        if (dto.getTrabajadores() != null) {
            this.trabajadores = dto.getTrabajadores().stream()
                    .map(TrabajadorDiaResponse::new)
                    .collect(Collectors.toList());
        }
    }
}