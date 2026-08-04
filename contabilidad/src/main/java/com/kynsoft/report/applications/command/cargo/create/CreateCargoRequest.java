package com.kynsoft.report.applications.command.cargo.create;

import java.math.BigDecimal;

public class CreateCargoRequest {
    private String name;
    private String description;
    private BigDecimal salarioEscala;

    public CreateCargoRequest() {}

    public CreateCargoRequest(String name, String description, BigDecimal salarioEscala) {
        this.name = name;
        this.description = description;
        this.salarioEscala = salarioEscala;
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
