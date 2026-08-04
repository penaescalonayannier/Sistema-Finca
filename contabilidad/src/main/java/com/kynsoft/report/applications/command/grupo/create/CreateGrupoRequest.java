package com.kynsoft.report.applications.command.grupo.create;

import java.util.UUID;

public class CreateGrupoRequest {
    private String nombre;
    private String descripcion;
    private UUID jefeId;

    public CreateGrupoRequest() {}

    public CreateGrupoRequest(String nombre, String descripcion, UUID jefeId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.jefeId = jefeId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public UUID getJefeId() {
        return jefeId;
    }

    public void setJefeId(UUID jefeId) {
        this.jefeId = jefeId;
    }
}
