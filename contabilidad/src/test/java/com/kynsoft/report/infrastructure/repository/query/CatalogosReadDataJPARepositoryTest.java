package com.kynsoft.report.infrastructure.repository.query;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contrato de carga para DTOs que se construyen tras cerrar el EntityManager de lectura CQRS.
 */
class CatalogosReadDataJPARepositoryTest {

    @Test
    void fincaDebeTraerResponsableEnDetallesYListados() throws Exception {
        assertGraph(FincaReadDataJPARepository.class.getMethod("findById", UUID.class), "responsable");
        assertGraph(FincaReadDataJPARepository.class.getMethod("findByCode", String.class), "responsable");
        assertGraph(FincaReadDataJPARepository.class.getMethod("findAll", Specification.class, Pageable.class), "responsable");
    }

    @Test
    void usuariosDebenTraerTrabajadorYSuFinca() throws Exception {
        for (Method method : List.of(
                UsuarioReadDataJPARepository.class.getMethod("findById", UUID.class),
                UsuarioReadDataJPARepository.class.getMethod("findByUsername", String.class),
                UsuarioReadDataJPARepository.class.getMethod("findByTrabajadorId", UUID.class),
                UsuarioReadDataJPARepository.class.getMethod("findAllActivos"),
                UsuarioReadDataJPARepository.class.getMethod("findByFincaId", UUID.class))) {
            assertGraph(method, "trabajador", "trabajador.finca");
        }
    }

    @Test
    void gruposDebenTraerJefeYTrabajadoresParaElDto() throws Exception {
        assertGraph(GrupoReadDataJPARepository.class.getMethod("findById", UUID.class), "jefe", "trabajadores");
        assertGraph(GrupoReadDataJPARepository.class.getMethod("findAll", Specification.class, Pageable.class), "jefe", "trabajadores");
    }

    @Test
    void consultasDeTrabajadorDebenTraerTodasLasRelacionesDelDto() throws Exception {
        List<String> expected = List.of("finca", "grupo", "cargo", "plaza");
        assertGraph(TrabajadorReadDataJPARepository.class.getMethod("findByRuc", String.class), expected);
        assertGraph(TrabajadorReadDataJPARepository.class.getMethod("findAllById", Iterable.class), expected);
        assertGraph(TrabajadorReadDataJPARepository.class.getMethod("findAll", Specification.class, Pageable.class), expected);
    }

    private void assertGraph(Method method, String... expectedPaths) {
        assertGraph(method, List.of(expectedPaths));
    }

    private void assertGraph(Method method, List<String> expectedPaths) {
        EntityGraph graph = method.getAnnotation(EntityGraph.class);
        assertNotNull(graph, () -> method + " debe declarar EntityGraph");
        assertTrue(List.of(graph.attributePaths()).containsAll(expectedPaths),
                () -> method + " debe cargar " + expectedPaths);
    }
}
