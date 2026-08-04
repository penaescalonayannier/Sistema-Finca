package com.kynsoft.report.applications.query.responseObject;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.GrupoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class GrupoResponse implements IResponse {
    private UUID id;
    private String nombre;
    private String descripcion;
    private UUID jefeId;
    private JefeGrupoResponse jefe;
    private List<TrabajadorGrupoResponse> trabajadores;

    public GrupoResponse(GrupoDto grupo) {
        this.id = grupo.getId();
        this.nombre = grupo.getNombre();
        this.descripcion = grupo.getDescripcion();
        this.jefeId = grupo.getJefeId();
        this.trabajadores = grupo.getTrabajadores() != null ?
                grupo.getTrabajadores().stream()
                    .map(TrabajadorGrupoResponse::new)
                    .collect(Collectors.toList())
                : java.util.Collections.emptyList();
        this.jefe = grupo.getJefe() != null ? new JefeGrupoResponse(grupo.getJefe()) : null;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class JefeGrupoResponse {
        private UUID id;
        private String nombre;
        private String ruc;
        private String cargoName;

        public JefeGrupoResponse(com.kynsoft.report.domain.dto.TrabajadorDto jefe) {
            this.id = jefe.getId();
            this.nombre = jefe.getNombre();
            this.ruc = jefe.getRuc();
            this.cargoName = jefe.getCargoName();
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class TrabajadorGrupoResponse {
        private UUID id;
        private String nombre;
        private String ruc;
        private String cargoName;

        public TrabajadorGrupoResponse(com.kynsoft.report.domain.dto.TrabajadorDto trabajador) {
            this.id = trabajador.getId();
            this.nombre = trabajador.getNombre();
            this.ruc = trabajador.getRuc();
            this.cargoName = trabajador.getCargoName();
        }
    }
}
