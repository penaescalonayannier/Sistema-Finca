package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Trabajador;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrabajadorReadDataJPARepositoryTest {

    @Test
    void consultasUsadasPorReportesDebenPrecargarCargo() throws NoSuchMethodException {
        Method todos = TrabajadorReadDataJPARepository.class.getMethod("findAll");
        Method filtrada = TrabajadorReadDataJPARepository.class.getMethod("findAll", Specification.class);

        for (Method metodo : List.of(todos, filtrada)) {
            EntityGraph graph = metodo.getAnnotation(EntityGraph.class);
            assertNotNull(graph);
            assertTrue(List.of(graph.attributePaths()).contains("cargo"));
        }
    }
}
