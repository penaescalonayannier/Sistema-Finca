package com.kynsoft.report.applications.query.responseObject;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TrabajadorHorasExcedidasResponse implements IResponse {

    private String trabajadorId;
    private String nombre;
    private String ruc;
    private String cargo;
    private List<DiaExcedidoResponse> diasExcedidos;

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class DiaExcedidoResponse {
        private String fecha;
        private double horas;
    }
}
