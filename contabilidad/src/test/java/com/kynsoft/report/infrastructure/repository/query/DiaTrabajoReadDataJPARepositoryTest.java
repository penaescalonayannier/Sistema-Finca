package com.kynsoft.report.infrastructure.repository.query;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiaTrabajoReadDataJPARepositoryTest {

    @Test
    void consultasGenericasDebenCargarElReporteParaValidarYMapear() throws NoSuchMethodException {
        Method findById = DiaTrabajoReadDataJPARepository.class.getMethod("findById", UUID.class);
        Method findAll = DiaTrabajoReadDataJPARepository.class
                .getMethod("findAll", Specification.class, Pageable.class);

        for (Method metodo : List.of(findById, findAll)) {
            EntityGraph graph = metodo.getAnnotation(EntityGraph.class);
            assertNotNull(graph);
            assertTrue(List.of(graph.attributePaths()).contains("reporte"));
        }
    }

    @Test
    void consultasMensualesDebenPrecargarResponsableYCargo() throws NoSuchMethodException {
        Method porPeriodo = DiaTrabajoReadDataJPARepository.class.getMethod(
                "findByYearAndMesWithTrabajadores", String.class, String.class);
        Method porFinca = DiaTrabajoReadDataJPARepository.class.getMethod(
                "findByYearAndMesAndFincaIdWithTrabajadores", String.class, String.class, UUID.class);

        for (Method metodo : List.of(porPeriodo, porFinca)) {
            Query query = metodo.getAnnotation(Query.class);
            assertNotNull(query);
            assertTrue(query.value().contains("FETCH dr.trabajadorResponsable"));
            assertTrue(query.value().contains("FETCH t.cargo"));
        }
    }
}
