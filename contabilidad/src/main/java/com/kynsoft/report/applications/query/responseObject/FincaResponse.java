package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.FincaDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FincaResponse implements IResponse {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private String direccion;
    private String telefono;
    private UUID responsableId;
    private String responsableNombre;
    private Double area;
    private Boolean activo;
    private Long cantidadTrabajadores;
    private Long cantidadProductos;

    public FincaResponse(FincaDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.direccion = dto.getDireccion();
        this.telefono = dto.getTelefono();
        this.responsableId = dto.getResponsableId();
        this.responsableNombre = dto.getResponsableNombre();
        this.area = dto.getArea();
        this.activo = dto.getActivo();
        this.cantidadTrabajadores = dto.getCantidadTrabajadores();
        this.cantidadProductos = dto.getCantidadProductos();
    }
}