package com.kynsoft.report.infrastructure.repository.query;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contratos contra regresiones LazyInitialization en el lado de lectura CQRS.
 * Los DTO de estos documentos se construyen una vez cerrada la sesión de
 * lectura, por lo que no pueden depender de proxies no inicializados.
 */
class DocumentosComercialesReadDataJPARepositoryTest {

    @Test
    void salidaDebeCargarDocumentoProductoEItemsEnOperacionesGenericas() throws NoSuchMethodException {
        Method porId = SalidaReadDataJPARepository.class.getMethod("findById", UUID.class);
        Method listado = SalidaReadDataJPARepository.class
                .getMethod("findAll", Specification.class, Pageable.class);
        Method porTipo = SalidaReadDataJPARepository.class.getMethod("findByTipo",
                com.kynsoft.report.domain.dto.TipoSalida.class);
        Method porProducto = SalidaReadDataJPARepository.class.getMethod("findByFincaProductoId", UUID.class);

        for (Method method : List.of(porId, listado, porTipo, porProducto)) {
            EntityGraph graph = method.getAnnotation(EntityGraph.class);
            assertNotNull(graph, method.getName());
            List<String> paths = List.of(graph.attributePaths());
            assertTrue(paths.containsAll(List.of("fincaProducto", "fincaProducto.finca",
                    "fincaProducto.producto", "items", "items.trabajador",
                    "items.fincaProducto", "items.fincaProducto.producto")), method.getName());
        }
    }

    @Test
    void consultasDeSalidasPorPeriodoDebenTraerTodasLasRelacionesDelDocumento() throws NoSuchMethodException {
        Method porFecha = SalidaReadDataJPARepository.class.getMethod("findByFechaBetween",
                LocalDateTime.class, LocalDateTime.class);
        Method activas = SalidaReadDataJPARepository.class.getMethod("findByFechaAndActivo",
                LocalDateTime.class, LocalDateTime.class);

        for (Method method : List.of(porFecha, activas)) {
            Query query = method.getAnnotation(Query.class);
            assertNotNull(query);
            assertTrue(query.value().contains("FETCH fp.finca"));
            assertTrue(query.value().contains("FETCH s.items"));
            assertTrue(query.value().contains("FETCH i.trabajador"));
            assertTrue(query.value().contains("FETCH ifp.producto"));
        }
    }

    @Test
    void pagosYDeudasDebenTraerTrabajadorYFincaAlMapearDtos() throws NoSuchMethodException {
        Method pagos = PagoDeudaReadDataJPARepository.class
                .getMethod("findAll", Specification.class, Pageable.class);
        Method pago = PagoDeudaReadDataJPARepository.class.getMethod("findById", UUID.class);
        Method pagosTrabajador = PagoDeudaReadDataJPARepository.class
                .getMethod("findByTrabajadorIdOrderByFechaDesc", UUID.class);
        Method deuda = DeudaTrabajadorReadDataJPARepository.class.getMethod("findById", UUID.class);
        Method deudas = DeudaTrabajadorReadDataJPARepository.class
                .getMethod("findAll", Specification.class, Pageable.class);
        Method deudaTrabajador = DeudaTrabajadorReadDataJPARepository.class
                .getMethod("findByTrabajadorId", UUID.class);

        for (Method method : List.of(pagos, pago, pagosTrabajador)) {
            EntityGraph graph = method.getAnnotation(EntityGraph.class);
            assertNotNull(graph, method.getName());
            assertTrue(List.of(graph.attributePaths())
                    .containsAll(List.of("trabajador", "trabajador.finca", "finca")), method.getName());
        }
        for (Method method : List.of(deuda, deudas, deudaTrabajador)) {
            EntityGraph graph = method.getAnnotation(EntityGraph.class);
            assertNotNull(graph, method.getName());
            assertTrue(List.of(graph.attributePaths()).contains("trabajador"), method.getName());
        }
    }

    @Test
    void consultasDePagoPorPeriodoDebenIncluirFincaDelTrabajador() throws NoSuchMethodException {
        Method todas = PagoDeudaReadDataJPARepository.class.getMethod("findByFechaBetween",
                LocalDateTime.class, LocalDateTime.class);
        Method porFinca = PagoDeudaReadDataJPARepository.class.getMethod("findByFincaIdAndFechaBetween",
                UUID.class, LocalDateTime.class, LocalDateTime.class);

        for (Method method : List.of(todas, porFinca)) {
            Query query = method.getAnnotation(Query.class);
            assertNotNull(query);
            assertTrue(query.value().contains("FETCH t.finca"));
            assertTrue(query.value().contains("FETCH p.finca"));
        }
    }
}
