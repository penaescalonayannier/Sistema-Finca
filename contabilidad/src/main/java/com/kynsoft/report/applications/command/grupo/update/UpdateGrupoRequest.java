package com.kynsoft.report.applications.command.grupo.update;

import java.util.UUID;

public class UpdateGrupoRequest {
    private UUID id;
    private String nombre;
    private String descripcion;
    private UUID jefeId;

    public UpdateGrupoRequest() {}

    public UpdateGrupoRequest(UUID id, String nombre, String descripcion, UUID jefeId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.jefeId = jefeId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
