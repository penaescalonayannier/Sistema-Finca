package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrabajadorConsolidadoResponse implements IResponse {
    private String trabajadorId;
    private String nombre;
    private String ruc;
    private String cargo;
    private String cuenta;
    private Map<Integer, String> horasPorDia;
    private Integer totalHoras;
}