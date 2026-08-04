package com.kynsoft.report.applications.command.cargo.update;

import java.math.BigDecimal;
import java.util.UUID;

public class UpdateCargoRequest {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal salarioEscala;

    public UpdateCargoRequest() {}

    public UpdateCargoRequest(UUID id, String name, String description, BigDecimal salarioEscala) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.salarioEscala = salarioEscala;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getSalarioEscala() {
        return salarioEscala;
    }

    public void setSalarioEscala(BigDecimal salarioEscala) {
        this.salarioEscala = salarioEscala;
    }
}
