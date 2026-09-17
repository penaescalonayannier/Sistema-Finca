package com.kynsoft.report.infrastructure.repository.query;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReporteReadDataJPARepositoryTest {

    @Test
    void laBusquedaPaginadaDebeCargarLasRelacionesUsadasPorElDto() throws NoSuchMethodException {
        Method findAll = ReporteReadDataJPARepository.class
                .getMethod("findAll", Specification.class, Pageable.class);

        EntityGraph entityGraph = findAll.getAnnotation(EntityGraph.class);

        assertNotNull(entityGraph);
        List<String> relaciones = List.of(entityGraph.attributePaths());
        assertTrue(relaciones.containsAll(List.of("tipoReporte", "tipoCultivo", "tipoAnimal")));
    }
}
