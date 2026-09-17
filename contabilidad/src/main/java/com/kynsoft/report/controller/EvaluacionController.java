package com.kynsoft.report.controller;

import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.CreateEvaluacionCommand;
import com.kynsoft.report.applications.command.DeleteEvaluacionCommand;
import com.kynsoft.report.applications.command.UpdateEvaluacionCommand;
import com.kynsoft.report.applications.command.evaluacion.CreateBatchEvaluacionCommand;
import com.kynsoft.report.applications.command.evaluacion.CreateBatchEvaluacionRequest;
import com.kynsoft.report.applications.command.message.CreateEvaluacionMessage;
import com.kynsoft.report.applications.command.message.DeleteEvaluacionMessage;
import com.kynsoft.report.applications.command.message.UpdateEvaluacionMessage;
import com.kynsoft.report.applications.query.GetEvaluacionQuery;
import com.kynsoft.report.applications.query.SearchEvaluacionQuery;
import com.kynsoft.report.applications.query.responseObject.EvaluacionResponse;
import com.kynsoft.report.domain.dto.EvaluacionDto;
import com.kynsoft.report.domain.dto.CriterioEvaluacionDto;
import com.kynsoft.report.domain.dto.EstadoEvaluacion;
import com.kynsoft.report.domain.dto.GrupoDto;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.IEvaluacionService;
import com.kynsoft.report.domain.services.ICriterioEvaluacionService;
import com.kynsoft.report.domain.services.IGrupoService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/evaluacion")
public class EvaluacionController {

    private final IMediator mediator;
    private final IGrupoService grupoService;
    private final IEvaluacionService evaluacionService;
    private final ITrabajadorService trabajadorService;
    private final ICriterioEvaluacionService criterioEvaluacionService;

    public EvaluacionController(IMediator mediator, IGrupoService grupoService,
                               IEvaluacionService evaluacionService, ITrabajadorService trabajadorService,
                               ICriterioEvaluacionService criterioEvaluacionService) {
        this.mediator = mediator;
        this.grupoService = grupoService;
        this.evaluacionService = evaluacionService;
        this.trabajadorService = trabajadorService;
        this.criterioEvaluacionService = criterioEvaluacionService;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateEvaluacionCommand.CreateEvaluacionRequest request) {
        CreateEvaluacionCommand createCommand = CreateEvaluacionCommand.fromRequest(request);
        mediator.send(createCommand);

        return ResponseEntity.ok("Evaluación creada exitosamente");
    }

    @PostMapping("/batch")
    public ResponseEntity<?> createBatch(@RequestBody CreateBatchEvaluacionRequest request) {
        CreateBatchEvaluacionCommand command = CreateBatchEvaluacionCommand.fromRequest(request);
        mediator.send(command);

        return ResponseEntity.ok(request.getEvaluaciones().size() + " evaluaciones creadas exitosamente");
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluacionResponse> findById(@PathVariable UUID id) {
        GetEvaluacionQuery query = new GetEvaluacionQuery(id);
        EvaluacionResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<?> getByGrupo(@PathVariable UUID grupoId) {
        try {
            // Obtener el grupo para extraer su jefeId
            GrupoDto grupo = grupoService.findById(grupoId);

            if (grupo == null) {
                java.util.Map<String, Object> response = new java.util.HashMap<>();
                response.put("data", new java.util.ArrayList<>());
                response.put("total", 0);
                return ResponseEntity.ok(response);
            }

            UUID jefeId = grupo.getJefeId();

            // Obtener todas las evaluaciones
            SearchRequest request = new SearchRequest();
            request.setPageSize(1000);
            request.setPage(0);

            Pageable pageable = PageableUtil.createPageable(request);
            SearchEvaluacionQuery query = new SearchEvaluacionQuery(pageable, java.util.List.of());
            PaginatedResponse paginatedData = mediator.send(query);

            // Acceder al contenido de PaginatedResponse usando reflection seguro
            java.util.List<java.util.Map<String, Object>> evaluacionesEnriquecidas = new java.util.ArrayList<>();

            if (paginatedData != null) {
                try {
                    // Descubrir los campos disponibles en PaginatedResponse
                    java.lang.reflect.Field[] fields = paginatedData.getClass().getDeclaredFields();

                    // Intentar acceder a campos comunes
                    Object contentObj = null;
                    for (java.lang.reflect.Field field : fields) {
                        field.setAccessible(true);
                        if (field.getName().equalsIgnoreCase("data") || field.getName().equalsIgnoreCase("content") || field.getName().equalsIgnoreCase("items")) {
                            contentObj = field.get(paginatedData);
                            break;
                        }
                    }

                    if (contentObj instanceof java.util.List) {
                        @SuppressWarnings("unchecked")
                        java.util.List<Object> items = (java.util.List<Object>) contentObj;

                        // Crear mapa de trabajadores por ID para búsqueda rápida
                        java.util.Map<UUID, String> trabajadoresMap = new java.util.HashMap<>();
                        if (grupo.getTrabajadores() != null) {
                            for (com.kynsoft.report.domain.dto.TrabajadorDto trab : grupo.getTrabajadores()) {
                                trabajadoresMap.put(trab.getId(), trab.getNombre());
                            }
                        }

                        for (Object item : items) {
                            if (item instanceof EvaluacionResponse) {
                                EvaluacionResponse eval = (EvaluacionResponse) item;
                                if (jefeId.equals(eval.getJefeId())) {
                                    // Enriquecer con nombre del trabajador
                                    java.util.Map<String, Object> evalEnriquecida = new java.util.HashMap<>();
                                    evalEnriquecida.put("id", eval.getId());
                                    evalEnriquecida.put("grupoId", eval.getGrupoId());
                                    evalEnriquecida.put("trabajadorId", eval.getTrabajadorId());
                                    evalEnriquecida.put("trabajadorNombre", trabajadoresMap.getOrDefault(eval.getTrabajadorId(), "Sin nombre"));
                                    evalEnriquecida.put("jefeId", eval.getJefeId());
                                    evalEnriquecida.put("mes", eval.getMes());
                                    evalEnriquecida.put("year", eval.getYear());
                                    evalEnriquecida.put("calificacion", eval.getCalificacion());
                                    evalEnriquecida.put("comentarios", eval.getComentarios());
                                    evalEnriquecida.put("fechaEvaluacion", eval.getFechaEvaluacion());
                                    evalEnriquecida.put("estado", eval.getEstado());
                                    evalEnriquecida.put("evidencia", eval.getEvidencia());
                                    evalEnriquecida.put("criteriosAplicados", eval.getCriteriosAplicados());
                                    evalEnriquecida.put("constanciaJefe", eval.getConstanciaJefe());
                                    evalEnriquecida.put("constanciaTrabajador", eval.getConstanciaTrabajador());
                                    evalEnriquecida.put("fechaEnvio", eval.getFechaEnvio());
                                    evalEnriquecida.put("fechaCierre", eval.getFechaCierre());
                                    evaluacionesEnriquecidas.add(evalEnriquecida);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error accediendo a contenido de PaginatedResponse: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Devolver estructura que espera el frontend
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("data", evaluacionesEnriquecidas);
            response.put("total", evaluacionesEnriquecidas.size());
            response.put("grupoNombre", grupo.getNombre());
            response.put("jefeNombre", grupo.getJefe() != null ? grupo.getJefe().getNombre() : "Sin asignar");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error en getByGrupo: " + e.getMessage());
            e.printStackTrace();

            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("data", new java.util.ArrayList<>());
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateEvaluacionCommand.UpdateEvaluacionRequest request) {
        request.setId(id);
        UpdateEvaluacionCommand updateCommand = UpdateEvaluacionCommand.fromRequest(request);
        mediator.send(updateCommand);

        return ResponseEntity.ok("Evaluación actualizada exitosamente");
    }

    /** Cambia el ciclo sin reutilizar la edición ordinaria; CERRADA queda inmutable. */
    @PutMapping("/{id}/estado")
    public ResponseEntity<EvaluacionResponse> cambiarEstado(@PathVariable UUID id,
            @RequestBody CambioEstadoEvaluacionRequest request) {
        EvaluacionDto evaluacion = evaluacionService.cambiarEstado(id, request.getEstado(),
                request.getConstanciaJefe(), request.getConstanciaTrabajador(), request.getObservacionesCierre());
        return ResponseEntity.ok(new EvaluacionResponse(evaluacion));
    }

    @GetMapping("/criterios")
    public ResponseEntity<java.util.List<CriterioEvaluacionDto>> listarCriterios(
            @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return ResponseEntity.ok(criterioEvaluacionService.listar(incluirInactivos));
    }

    @PostMapping("/criterios")
    public ResponseEntity<CriterioEvaluacionDto> guardarCriterio(@RequestBody CriterioEvaluacionDto criterio) {
        return ResponseEntity.ok(criterioEvaluacionService.guardar(criterio));
    }

    @DeleteMapping("/criterios/{id}")
    public ResponseEntity<Void> desactivarCriterio(@PathVariable UUID id) {
        criterioEvaluacionService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @lombok.Getter
    @lombok.Setter
    public static class CambioEstadoEvaluacionRequest {
        private EstadoEvaluacion estado;
        private String constanciaJefe;
        private String constanciaTrabajador;
        private String observacionesCierre;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteEvaluacionCommand deleteCommand = new DeleteEvaluacionCommand(id);
        mediator.send(deleteCommand);

        return ResponseEntity.ok("Evaluación eliminada exitosamente");
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        SearchEvaluacionQuery query = new SearchEvaluacionQuery(pageable, request.getFilter());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/por-periodo")
    public ResponseEntity<?> getByPeriodo(@RequestParam String mes, @RequestParam Integer year) {
        try {
            java.util.List<EvaluacionDto> evaluaciones = evaluacionService.findByMesAndYear(mes, year);

            java.util.List<java.util.Map<String, Object>> resultado = evaluaciones.stream().map(e -> {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", e.getId());
                map.put("trabajadorId", e.getTrabajadorId());
                map.put("jefeId", e.getJefeId());
                map.put("grupoId", e.getGrupoId());
                map.put("mes", e.getMes());
                map.put("year", e.getYear());
                map.put("calificacion", e.getCalificacion());
                map.put("comentarios", e.getComentarios());
                map.put("fechaEvaluacion", e.getFechaEvaluacion());
                map.put("estado", e.getEstado());
                map.put("evidencia", e.getEvidencia());
                map.put("criteriosAplicados", e.getCriteriosAplicados());
                map.put("constanciaJefe", e.getConstanciaJefe());
                map.put("constanciaTrabajador", e.getConstanciaTrabajador());
                map.put("fechaEnvio", e.getFechaEnvio());
                map.put("fechaCierre", e.getFechaCierre());

                // Get worker name
                try {
                    TrabajadorDto trabajador = trabajadorService.findById(e.getTrabajadorId());
                    map.put("trabajadorNombre", trabajador.getNombre());
                    map.put("trabajadorRuc", trabajador.getRuc());
                    map.put("trabajadorCargo", trabajador.getCargoName());
                } catch (Exception ex) {
                    map.put("trabajadorNombre", "Sin nombre");
                    map.put("trabajadorRuc", null);
                    map.put("trabajadorCargo", null);
                }

                return map;
            }).collect(Collectors.toList());

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("data", resultado);
            response.put("total", resultado.size());
            response.put("mes", mes);
            response.put("year", year);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("data", new java.util.ArrayList<>());
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/consolidado-mensual")
    public ResponseEntity<?> getConsolidadoMensual(@RequestParam String mes, @RequestParam Integer year) {
        try {
            java.util.List<EvaluacionDto> evaluaciones = evaluacionService.findByMesAndYear(mes, year);

            // Group by trabajadorId and calculate stats
            java.util.Map<UUID, java.util.List<EvaluacionDto>> porTrabajador = evaluaciones.stream()
                    .collect(Collectors.groupingBy(EvaluacionDto::getTrabajadorId));

            java.util.List<java.util.Map<String, Object>> consolidado = new java.util.ArrayList<>();

            for (java.util.Map.Entry<UUID, java.util.List<EvaluacionDto>> entry : porTrabajador.entrySet()) {
                java.util.Map<String, Object> item = new java.util.HashMap<>();
                java.util.List<EvaluacionDto> evals = entry.getValue();
                EvaluacionDto firstEval = evals.get(0);

                item.put("trabajadorId", entry.getKey());
                item.put("calificacion", firstEval.getCalificacion());
                item.put("comentarios", firstEval.getComentarios());

                try {
                    TrabajadorDto trabajador = trabajadorService.findById(entry.getKey());
                    item.put("trabajadorNombre", trabajador.getNombre());
                    item.put("trabajadorRuc", trabajador.getRuc());
                    item.put("trabajadorCargo", trabajador.getCargoName());
                    item.put("grupoId", trabajador.getGrupoId());
                    item.put("grupoNombre", trabajador.getGrupoNombre() != null ? trabajador.getGrupoNombre() : "Sin Grupo");
                } catch (Exception ex) {
                    item.put("trabajadorNombre", "Sin nombre");
                    item.put("trabajadorRuc", null);
                    item.put("trabajadorCargo", null);
                    item.put("grupoId", null);
                    item.put("grupoNombre", "Sin Grupo");
                }

                consolidado.add(item);
            }

            // Sort by grupo then by name
            consolidado.sort((a, b) -> {
                String grupoA = (String) a.getOrDefault("grupoNombre", "Sin Grupo");
                String grupoB = (String) b.getOrDefault("grupoNombre", "Sin Grupo");
                int grupoCompare = grupoA.compareToIgnoreCase(grupoB);
                if (grupoCompare != 0) return grupoCompare;

                String nameA = (String) a.getOrDefault("trabajadorNombre", "");
                String nameB = (String) b.getOrDefault("trabajadorNombre", "");
                return nameA.compareToIgnoreCase(nameB);
            });

            // Calculate summary stats
            double promedioGeneral = consolidado.stream()
                    .mapToInt(m -> (Integer) m.getOrDefault("calificacion", 0))
                    .average()
                    .orElse(0.0);

            long superiores = consolidado.stream()
                    .filter(m -> ((Integer) m.getOrDefault("calificacion", 0)) >= 4)
                    .count();
            long adecuados = consolidado.stream()
                    .filter(m -> ((Integer) m.getOrDefault("calificacion", 0)) == 3)
                    .count();
            long deficientes = consolidado.stream()
                    .filter(m -> ((Integer) m.getOrDefault("calificacion", 0)) <= 2)
                    .count();

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("data", consolidado);
            response.put("total", consolidado.size());
            response.put("mes", mes);
            response.put("year", year);
            response.put("promedioGeneral", Math.round(promedioGeneral * 100.0) / 100.0);
            response.put("superiores", superiores);
            response.put("adecuados", adecuados);
            response.put("deficientes", deficientes);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("data", new java.util.ArrayList<>());
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/consolidado-trimestral")
    public ResponseEntity<?> getConsolidadoTrimestral(
            @RequestParam Integer year,
            @RequestParam String mesInicio,
            @RequestParam String mesFin) {
        try {
            // Get list of months in range
            java.util.List<String> mesesOrdenados = Arrays.asList(
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
            );

            int indexInicio = mesesOrdenados.indexOf(mesInicio);
            int indexFin = mesesOrdenados.indexOf(mesFin);

            if (indexInicio == -1 || indexFin == -1) {
                java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
                errorResponse.put("error", "Mes inválido");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            java.util.List<String> mesesRango = new java.util.ArrayList<>();
            for (int i = indexInicio; i <= indexFin; i++) {
                mesesRango.add(mesesOrdenados.get(i));
            }

            java.util.List<EvaluacionDto> evaluaciones = evaluacionService.findByYearAndMeses(year, mesesRango);

            // Group by trabajadorId
            java.util.Map<UUID, java.util.List<EvaluacionDto>> porTrabajador = evaluaciones.stream()
                    .collect(Collectors.groupingBy(EvaluacionDto::getTrabajadorId));

            java.util.List<java.util.Map<String, Object>> consolidado = new java.util.ArrayList<>();

            for (java.util.Map.Entry<UUID, java.util.List<EvaluacionDto>> entry : porTrabajador.entrySet()) {
                java.util.Map<String, Object> item = new java.util.HashMap<>();
                java.util.List<EvaluacionDto> evals = entry.getValue();

                item.put("trabajadorId", entry.getKey());

                // Calculate average
                double promedio = evals.stream()
                        .mapToInt(EvaluacionDto::getCalificacion)
                        .average()
                        .orElse(0.0);
                item.put("promedioCalificacion", Math.round(promedio * 100.0) / 100.0);
                item.put("cantidadEvaluaciones", evals.size());

                // Details per month
                java.util.Map<String, Integer> porMes = new java.util.HashMap<>();
                for (EvaluacionDto e : evals) {
                    porMes.put(e.getMes(), e.getCalificacion());
                }
                item.put("calificacionesPorMes", porMes);

                try {
                    TrabajadorDto trabajador = trabajadorService.findById(entry.getKey());
                    item.put("trabajadorNombre", trabajador.getNombre());
                    item.put("trabajadorRuc", trabajador.getRuc());
                    item.put("trabajadorCargo", trabajador.getCargoName());
                    item.put("grupoId", trabajador.getGrupoId());
                    item.put("grupoNombre", trabajador.getGrupoNombre() != null ? trabajador.getGrupoNombre() : "Sin Grupo");
                } catch (Exception ex) {
                    item.put("trabajadorNombre", "Sin nombre");
                    item.put("trabajadorRuc", null);
                    item.put("trabajadorCargo", null);
                    item.put("grupoId", null);
                    item.put("grupoNombre", "Sin Grupo");
                }

                consolidado.add(item);
            }

            // Sort by grupo then by name
            consolidado.sort((a, b) -> {
                String grupoA = (String) a.getOrDefault("grupoNombre", "Sin Grupo");
                String grupoB = (String) b.getOrDefault("grupoNombre", "Sin Grupo");
                int grupoCompare = grupoA.compareToIgnoreCase(grupoB);
                if (grupoCompare != 0) return grupoCompare;

                String nameA = (String) a.getOrDefault("trabajadorNombre", "");
                String nameB = (String) b.getOrDefault("trabajadorNombre", "");
                return nameA.compareToIgnoreCase(nameB);
            });

            // Calculate summary stats
            double promedioGeneral = consolidado.stream()
                    .mapToDouble(m -> (Double) m.getOrDefault("promedioCalificacion", 0.0))
                    .average()
                    .orElse(0.0);

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("data", consolidado);
            response.put("total", consolidado.size());
            response.put("year", year);
            response.put("mesInicio", mesInicio);
            response.put("mesFin", mesFin);
            response.put("meses", mesesRango);
            response.put("promedioGeneral", Math.round(promedioGeneral * 100.0) / 100.0);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("data", new java.util.ArrayList<>());
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/trabajadores-para-evaluar")
    public ResponseEntity<?> getTrabajadoresParaEvaluar() {
        try {
            java.util.List<TrabajadorDto> trabajadores = trabajadorService.findAllActivos();

            java.util.List<java.util.Map<String, Object>> resultado = trabajadores.stream().map(t -> {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", t.getId());
                map.put("nombre", t.getNombre());
                map.put("ruc", t.getRuc());
                map.put("cargo", t.getCargoName());
                map.put("grupoId", t.getGrupoId());
                map.put("grupoNombre", t.getGrupoNombre());
                map.put("fincaId", t.getFincaId());
                map.put("fincaNombre", t.getFincaName());
                return map;
            }).collect(Collectors.toList());

            // Sort by name
            resultado.sort((a, b) -> {
                String nameA = (String) a.getOrDefault("nombre", "");
                String nameB = (String) b.getOrDefault("nombre", "");
                return nameA.compareToIgnoreCase(nameB);
            });

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("data", resultado);
            response.put("total", resultado.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("data", new java.util.ArrayList<>());
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/anos-disponibles")
    public ResponseEntity<?> getAnosDisponibles() {
        try {
            java.util.List<Integer> years = evaluacionService.findDistinctYears();
            if (years.isEmpty()) {
                years = Arrays.asList(java.time.Year.now().getValue());
            }
            return ResponseEntity.ok(years);
        } catch (Exception e) {
            return ResponseEntity.ok(Arrays.asList(java.time.Year.now().getValue()));
        }
    }

    @GetMapping("/meses-disponibles")
    public ResponseEntity<?> getMesesDisponibles(@RequestParam Integer year) {
        try {
            java.util.List<String> meses = evaluacionService.findDistinctMesesByYear(year);
            return ResponseEntity.ok(meses);
        } catch (Exception e) {
            return ResponseEntity.ok(new java.util.ArrayList<>());
        }
    }
}
