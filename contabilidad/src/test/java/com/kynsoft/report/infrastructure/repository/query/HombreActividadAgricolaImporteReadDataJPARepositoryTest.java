package com.kynsoft.report.infrastructure.repository.query;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HombreActividadAgricolaImporteReadDataJPARepositoryTest {

    @Test
    void consultasDebenPrecargarElDetalleCompuestoDelParteAgricola() throws NoSuchMethodException {
        Method detalle = HombreActividadAgricolaImporteReadDataJPARepository.class.getMethod("findById", UUID.class);
        Method listado = HombreActividadAgricolaImporteReadDataJPARepository.class
                .getMethod("findAll", Specification.class, Pageable.class);

        for (Method metodo : List.of(detalle, listado)) {
            EntityGraph graph = metodo.getAnnotation(EntityGraph.class);
            assertNotNull(graph);
            assertTrue(List.of(graph.attributePaths()).containsAll(List.of(
                    "trabajador", "trabajador.finca", "trabajador.grupo", "trabajador.cargo",
                    "trabajador.plaza", "labor", "instrumento", "bloque", "bloque.finca",
                    "campo", "campo.bloque", "campo.bloque.finca", "campo.variedad", "campo.cepa")));
        }
    }
}
