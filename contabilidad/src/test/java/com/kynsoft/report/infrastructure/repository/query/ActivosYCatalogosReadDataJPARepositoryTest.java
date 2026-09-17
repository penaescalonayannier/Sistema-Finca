package com.kynsoft.report.infrastructure.repository.query;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.EntityGraph;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActivosYCatalogosReadDataJPARepositoryTest {

    @Test
    void activosDebenCargarLaFincaYGrupoRequeridosPorSusDtos() throws NoSuchMethodException {
        Method animal = ActivoAnimalReadRepository.class.getMethod("findById", UUID.class);
        Method fijo = ActivoFijoTangibleReadRepository.class.getMethod("findById", UUID.class);
        Method plantacion = PlantacionPermanenteReadRepository.class.getMethod("findById", UUID.class);

        assertGraph(animal, "finca");
        assertGraph(fijo, "grupo", "finca");
        assertGraph(plantacion, "finca");
    }

    @Test
    void catalogosCompuestosDebenCargarSusRelacionesParaMapear() throws NoSuchMethodException {
        Method campo = CampoReadDataJPARepository.class.getMethod("findById", UUID.class);
        Method bloque = BloqueReadDataJPARepository.class.getMethod("findById", UUID.class);
        Method tipoReporte = TipoReporteReadDataJPARepository.class.getMethod("findById", UUID.class);
        Method configuracion = ConfiguracionNumeracionReadDataJPARepository.class.getMethod("findById", UUID.class);

        assertGraph(campo, "bloque", "bloque.finca", "variedad", "cepa");
        assertGraph(bloque, "finca");
        assertGraph(tipoReporte, "tipoCultivoAuto");
        assertGraph(configuracion, "finca");
    }

    private void assertGraph(Method method, String... paths) {
        EntityGraph graph = method.getAnnotation(EntityGraph.class);
        assertNotNull(graph, () -> method + " debe declarar EntityGraph");
        assertTrue(List.of(graph.attributePaths()).containsAll(List.of(paths)));
    }
}
