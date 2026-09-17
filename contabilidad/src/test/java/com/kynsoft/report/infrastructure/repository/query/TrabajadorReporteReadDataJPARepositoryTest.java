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

class TrabajadorReporteReadDataJPARepositoryTest {

    @Test
    void operacionesGenericasDebenCargarReporteYTrabajador() throws NoSuchMethodException {
        Method detalle = TrabajadorReporteReadDataJPARepository.class.getMethod("findById", UUID.class);
        Method listado = TrabajadorReporteReadDataJPARepository.class
                .getMethod("findAll", Specification.class, Pageable.class);

        EntityGraph detalleGraph = detalle.getAnnotation(EntityGraph.class);
        EntityGraph listadoGraph = listado.getAnnotation(EntityGraph.class);
        assertNotNull(detalleGraph);
        assertNotNull(listadoGraph);
        assertTrue(List.of(detalleGraph.attributePaths()).containsAll(List.of("reporte", "trabajador", "trabajador.cargo")));
        assertTrue(List.of(listadoGraph.attributePaths()).containsAll(List.of("reporte", "trabajador")));
    }

    @Test
    void detallePorReporteDebePrecargarCargo() throws NoSuchMethodException {
        Method porReporte = TrabajadorReporteReadDataJPARepository.class.getMethod("findByReporteId", UUID.class);
        Query query = porReporte.getAnnotation(Query.class);

        assertNotNull(query);
        assertTrue(query.value().contains("FETCH t.cargo"));
    }
}
