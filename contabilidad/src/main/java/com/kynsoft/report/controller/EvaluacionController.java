package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
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
import com.kynsoft.report.domain.dto.GrupoDto;
import com.kynsoft.report.domain.services.IGrupoService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/evaluacion")
public class EvaluacionController {

    private final IMediator mediator;
    private final IGrupoService grupoService;

    public EvaluacionController(IMediator mediator, IGrupoService grupoService) {
        this.mediator = mediator;
        this.grupoService = grupoService;
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
}
