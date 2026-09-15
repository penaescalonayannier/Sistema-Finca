package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.TrabajadorDto;
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
public class TrabajadorResponse implements IResponse {

    private UUID id;
    private String ruc;
    private String nombre;
    private String cuenta;
    private UUID fincaId;
    private String fincaCode;
    private String fincaName;
    private UUID grupoId;
    private String grupoNombre;
    private UUID cargoId;
    private String cargoName;
    private Boolean activo;

    public TrabajadorResponse(TrabajadorDto trabajador) {
        this.id = trabajador.getId();
        this.ruc = trabajador.getRuc();
        this.nombre = trabajador.getNombre();
        this.cuenta = trabajador.getCuenta();
        this.fincaId = trabajador.getFincaId();
        this.fincaCode = trabajador.getFincaCode();
        this.fincaName = trabajador.getFincaName();
        this.grupoId = trabajador.getGrupoId();
        this.grupoNombre = trabajador.getGrupoNombre();
        this.cargoId = trabajador.getCargoId();
        this.cargoName = trabajador.getCargoName();
        this.activo = trabajador.getActivo();
    }
}
