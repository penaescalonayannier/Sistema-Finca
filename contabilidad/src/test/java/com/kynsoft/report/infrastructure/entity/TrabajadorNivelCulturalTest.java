package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.TrabajadorDto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrabajadorNivelCulturalTest {

    @Test
    void conservaNivelCulturalAlConvertirEntreDtoYEntidad() {
        TrabajadorDto dto = TrabajadorDto.builder()
                .id(UUID.randomUUID())
                .ruc("90010112345")
                .nombre("Trabajador de prueba")
                .cuenta("001")
                .nivelCultural("Técnico medio")
                .build();

        Trabajador trabajador = new Trabajador(dto);

        assertEquals("Técnico medio", trabajador.getNivelCultural());
        assertEquals("Técnico medio", trabajador.toAggregate().getNivelCultural());
    }
}
