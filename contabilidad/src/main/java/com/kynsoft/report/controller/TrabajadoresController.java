package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.report.trabajador.upload.ImportTrabajadorCsvCommand;
import com.kynsoft.report.applications.command.trabajador.create.CreateTrabajadorCommand;
import com.kynsoft.report.applications.command.trabajador.create.CreateTrabajadorMessage;
import com.kynsoft.report.applications.command.trabajador.create.CreateTrabajadorRequest;
import com.kynsoft.report.applications.command.trabajador.delete.DeleteTrabajadorCommand;
import com.kynsoft.report.applications.command.trabajador.delete.DeleteTrabajadorMessage;
import com.kynsoft.report.applications.command.trabajador.update.UpdateTrabajadorCommand;
import com.kynsoft.report.applications.command.trabajador.update.UpdateTrabajadorMessage;
import com.kynsoft.report.applications.command.trabajador.update.UpdateTrabajadorRequest;
import com.kynsoft.report.applications.command.trabajador.asignarCargo.AsignarCargoTrabajadorCommand;
import com.kynsoft.report.applications.command.trabajador.asignarCargo.AsignarCargoTrabajadorMessage;
import com.kynsoft.report.applications.command.trabajador.asignarCargo.AsignarCargoTrabajadorRequest;
import com.kynsoft.report.applications.command.trabajador.asignarGrupo.AsignarGrupoTrabajadorCommand;
import com.kynsoft.report.applications.command.trabajador.asignarGrupo.AsignarGrupoTrabajadorMessage;
import com.kynsoft.report.applications.command.trabajador.asignarGrupo.AsignarGrupoTrabajadorRequest;
import com.kynsoft.report.applications.query.report.estadoCuenta.export.Response;
import com.kynsoft.report.applications.query.responseObject.TrabajadorResponse;
import com.kynsoft.report.applications.query.trabajador.export.GetExportNominaTrabajadorQuery;
import com.kynsoft.report.applications.query.trabajador.getById.FindTrabajadorByIdQuery;
import com.kynsoft.report.applications.query.trabajador.search.GetSearchTrabajadorQuery;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api/trabajadores")
public class TrabajadoresController {

    private final IMediator mediator;

    public TrabajadoresController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateTrabajadorRequest request) {
        CreateTrabajadorCommand createCommand = CreateTrabajadorCommand.fromRequest(request);
        CreateTrabajadorMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrabajadorResponse> findById(@PathVariable UUID id) {
        FindTrabajadorByIdQuery query = new FindTrabajadorByIdQuery(id);
        TrabajadorResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateTrabajadorRequest request) {
        UpdateTrabajadorCommand updateCommand = UpdateTrabajadorCommand.fromRequest(request, id);
        UpdateTrabajadorMessage response = mediator.send(updateCommand);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteTrabajadorCommand deleteCommand = new DeleteTrabajadorCommand(id);
        DeleteTrabajadorMessage response = mediator.send(deleteCommand);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchTrabajadorQuery query = new GetSearchTrabajadorQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/{trabajadorId}/asignar-cargo")
    public ResponseEntity<?> asignarCargo(@PathVariable UUID trabajadorId, @RequestBody AsignarCargoTrabajadorRequest request) {
        request.setTrabajadorId(trabajadorId);
        AsignarCargoTrabajadorCommand command = AsignarCargoTrabajadorCommand.fromRequest(request);
        AsignarCargoTrabajadorMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{trabajadorId}/asignar-grupo")
    public ResponseEntity<?> asignarGrupo(@PathVariable UUID trabajadorId, @RequestBody AsignarGrupoTrabajadorRequest request) {
        request.setTrabajadorId(trabajadorId);
        AsignarGrupoTrabajadorCommand command = AsignarGrupoTrabajadorCommand.fromRequest(request);
        AsignarGrupoTrabajadorMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/import-csv")
    public ResponseEntity<ICommandMessage> importCsv(@RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no debe estar vacío.");
        }

        ImportTrabajadorCsvCommand command = new ImportTrabajadorCsvCommand(file.getBytes());
        ICommandMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para exportar la nómina de trabajadores seleccionados.
     * @param ids Lista de UUIDs de los trabajadores.
     * @return Archivo Excel (.xls)
     */
    @PostMapping("/export")
    public ResponseEntity<StreamingResponseBody> exportNomina(@RequestBody List<UUID> ids) {
        GetExportNominaTrabajadorQuery query = new GetExportNominaTrabajadorQuery(ids);
        Response response = mediator.send(query);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"nomina_trabajadores.xls\"")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(response.getOutputStream());
    }
}
