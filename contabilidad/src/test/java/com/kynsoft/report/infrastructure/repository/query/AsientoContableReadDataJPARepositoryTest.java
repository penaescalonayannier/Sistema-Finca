package com.kynsoft.report.infrastructure.repository.query;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.EntityGraph;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsientoContableReadDataJPARepositoryTest {

    @Test
    void detalleYListadosDebenCargarLineasDelAsiento() throws NoSuchMethodException {
        Method porId = AsientoContableReadDataJPARepository.class.getMethod("findById", UUID.class);
        Method porPeriodo = AsientoContableReadDataJPARepository.class
                .getMethod("findByFechaBetween", LocalDate.class, LocalDate.class);

        for (Method metodo : List.of(porId, porPeriodo)) {
            EntityGraph graph = metodo.getAnnotation(EntityGraph.class);
            assertNotNull(graph);
            assertTrue(List.of(graph.attributePaths()).contains("lineas"));
        }
    }
}
