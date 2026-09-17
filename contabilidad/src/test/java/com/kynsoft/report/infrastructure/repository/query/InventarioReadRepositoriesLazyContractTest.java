package com.kynsoft.report.infrastructure.repository.query;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contrato de seguridad para el lado CQRS de lectura: estas entidades se
 * convierten a DTO después de cerrar el EntityManager de consulta. Por eso,
 * toda relación leída por el mapeo debe estar incluida en un EntityGraph.
 */
class InventarioReadRepositoriesLazyContractTest {

    private static final List<String> FINCA_PRODUCTO = List.of("finca", "producto");
    private static final List<String> ALMACEN_PRODUCTO = List.of(
            "almacen", "fincaProducto", "fincaProducto.finca", "fincaProducto.producto");
    private static final List<String> PRODUCCION = List.of(
            "finca", "producto", "trabajadorEntrega", "trabajadorRecibe");

    @Test
    void fincaProductoDebeMaterializarRelacionesEnListasAlertasYDetalle() throws Exception {
        assertGraph(FincaProductoReadDataJPARepository.class.getMethod("findById", UUID.class), FINCA_PRODUCTO);
        assertGraph(FincaProductoReadDataJPARepository.class.getMethod("findAll"), FINCA_PRODUCTO);
        assertGraph(FincaProductoReadDataJPARepository.class.getMethod("findAllById", Iterable.class), FINCA_PRODUCTO);
        assertGraph(FincaProductoReadDataJPARepository.class.getMethod(
                "findAll", Specification.class, Pageable.class), FINCA_PRODUCTO);
        assertGraph(FincaProductoReadDataJPARepository.class.getMethod(
                "findAll", Specification.class), FINCA_PRODUCTO);
        assertGraph(FincaProductoReadDataJPARepository.class.getMethod(
                "findByFincaIdAndActivoTrue", UUID.class), FINCA_PRODUCTO);
        assertGraph(FincaProductoReadDataJPARepository.class.getMethod(
                "findByFincaIdAndProductoId", UUID.class, UUID.class), FINCA_PRODUCTO);
    }

    @Test
    void almacenYStockPorAlmacenDebenMaterializarSusRelaciones() throws Exception {
        assertGraph(AlmacenReadDataJPARepository.class.getMethod("findById", UUID.class), List.of("finca"));
        assertGraph(AlmacenReadDataJPARepository.class.getMethod(
                "findByInventario", String.class), List.of("finca"));
        assertGraph(AlmacenFincaProductoReadDataJPARepository.class.getMethod(
                "findAll", Specification.class, Pageable.class), ALMACEN_PRODUCTO);
        assertGraph(AlmacenFincaProductoReadDataJPARepository.class.getMethod(
                "findByAlmacenIdAndActivoTrue", UUID.class), ALMACEN_PRODUCTO);
    }

    @Test
    void produccionTerminadaDebeMaterializarRelacionesEnTodosLosListadosPublicos() throws Exception {
        assertGraph(ProduccionTerminadaReadDataJPARepository.class.getMethod("findById", UUID.class), PRODUCCION);
        assertGraph(ProduccionTerminadaReadDataJPARepository.class.getMethod(
                "findAll", Specification.class, Pageable.class), PRODUCCION);
        assertGraph(ProduccionTerminadaReadDataJPARepository.class.getMethod(
                "findByProductoIdAndActivoTrue", UUID.class), PRODUCCION);
        assertGraph(ProduccionTerminadaReadDataJPARepository.class.getMethod(
                "findByFechaBetweenAndActivoTrue", LocalDateTime.class, LocalDateTime.class), PRODUCCION);
        assertGraph(ProduccionTerminadaReadDataJPARepository.class.getMethod(
                "findByTrabajadorEntregaIdAndActivoTrue", UUID.class), PRODUCCION);
        assertGraph(ProduccionTerminadaReadDataJPARepository.class.getMethod(
                "findByTrabajadorRecibeIdAndActivoTrue", UUID.class), PRODUCCION);
        assertGraph(ProduccionTerminadaReadDataJPARepository.class.getMethod(
                "findByFincaIdAndActivoTrue", UUID.class), PRODUCCION);
        assertGraph(ProduccionTerminadaReadDataJPARepository.class.getMethod(
                "findByFincaIdAndFechaBetweenAndActivoTrue", UUID.class, LocalDateTime.class, LocalDateTime.class), PRODUCCION);
    }

    private void assertGraph(Method method, List<String> expectedPaths) {
        EntityGraph graph = method.getAnnotation(EntityGraph.class);
        assertNotNull(graph, () -> method + " debe declarar EntityGraph");
        assertTrue(List.of(graph.attributePaths()).containsAll(expectedPaths),
                () -> method + " debe cargar " + expectedPaths);
    }
}
