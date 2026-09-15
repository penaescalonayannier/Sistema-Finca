package com.kynsoft.report.controller;

import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.salida.create.CreateSalidaCommand;
import com.kynsoft.report.applications.command.salida.create.CreateSalidaMessage;
import com.kynsoft.report.applications.command.salida.create.CreateSalidaRequest;
import com.kynsoft.report.applications.command.salida.consolidado.ValesConsolidadosRequest;
import com.kynsoft.report.applications.command.salida.delete.DeleteSalidaCommand;
import com.kynsoft.report.applications.command.salida.delete.DeleteSalidaMessage;
import com.kynsoft.report.applications.command.salida.update.UpdateSalidaCommand;
import com.kynsoft.report.applications.command.salida.update.UpdateSalidaMessage;
import com.kynsoft.report.applications.command.salida.update.UpdateSalidaRequest;
import com.kynsoft.report.applications.query.salida.getall.GetAllSalidaQuery;
import com.kynsoft.report.applications.query.salida.getbyid.FindSalidaByIdQuery;
import com.kynsoft.report.applications.query.responseObject.SalidaResponse;
import com.kynsoft.report.domain.dto.ConfiguracionEmpresaDto;
import com.kynsoft.report.domain.dto.DestinoSalida;
import com.kynsoft.report.domain.dto.SalidaDto;
import com.kynsoft.report.domain.services.IConfiguracionEmpresaService;
import com.kynsoft.report.domain.services.ISalidaService;
import com.kynsoft.report.infrastructure.services.FacturaPdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/salida")
public class SalidaController {

    private static final Logger log = LoggerFactory.getLogger(SalidaController.class);

    private final IMediator mediator;
    private final ISalidaService salidaService;
    private final FacturaPdfService facturaPdfService;
    private final IConfiguracionEmpresaService configuracionEmpresaService;

    public SalidaController(IMediator mediator, ISalidaService salidaService,
                            FacturaPdfService facturaPdfService,
                            IConfiguracionEmpresaService configuracionEmpresaService) {
        this.mediator = mediator;
        this.salidaService = salidaService;
        this.facturaPdfService = facturaPdfService;
        this.configuracionEmpresaService = configuracionEmpresaService;
    }

    @PostMapping
    public ResponseEntity<CreateSalidaMessage> create(@RequestBody CreateSalidaRequest request) {
        CreateSalidaCommand command = CreateSalidaCommand.fromRequest(request);
        CreateSalidaMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateSalidaMessage> update(@PathVariable UUID id, @RequestBody UpdateSalidaRequest request) {
        request.setId(id);
        UpdateSalidaCommand command = UpdateSalidaCommand.fromRequest(request);
        UpdateSalidaMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteSalidaMessage> delete(@PathVariable UUID id) {
        DeleteSalidaCommand command = new DeleteSalidaCommand(id);
        DeleteSalidaMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalidaResponse> findById(@PathVariable UUID id) {
        FindSalidaByIdQuery query = new FindSalidaByIdQuery(id);
        SalidaResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetAllSalidaQuery query = new GetAllSalidaQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/factura")
    public ResponseEntity<byte[]> descargarFactura(@PathVariable UUID id) {
        try {
            SalidaDto salida = salidaService.findById(id);

            // Obtener configuración de empresa (datos obligatorios según modelos cubanos)
            ConfiguracionEmpresaDto empresa = configuracionEmpresaService.findActive()
                    .orElse(ConfiguracionEmpresaDto.builder()
                            .nombre("El Coloso S.A.")
                            .codigo("")
                            .nit("")
                            .direccion("Delicias")
                            .municipio("Puerto Padre")
                            .provincia("Las Tunas")
                            .build());

            byte[] pdfBytes = facturaPdfService.generarFactura(salida, empresa);

            String tipoDoc = (salida.getTipo() == null || salida.getTipo().name().equals("VALE")) ? "Vale" : "Factura";
            String numero = salida.getNumero() != null ? salida.getNumero().replace("/", "-") : "sin-numero";
            String filename = tipoDoc + "_" + numero + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error al generar PDF para salida {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/vales/consolidado")
    public ResponseEntity<byte[]> descargarValesConsolidados(
            @RequestParam LocalDate fecha,
            @RequestParam DestinoSalida destino) {
        try {
            List<SalidaDto> vales = salidaService.findValesActivosPorFechaYDestino(fecha, destino);
            if (vales.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ConfiguracionEmpresaDto empresa = configuracionEmpresaService.findActive()
                    .orElse(ConfiguracionEmpresaDto.builder()
                            .nombre("El Coloso S.A.")
                            .codigo("")
                            .nit("")
                            .direccion("Delicias")
                            .municipio("Puerto Padre")
                            .provincia("Las Tunas")
                            .build());
            byte[] pdfBytes = facturaPdfService.generarValesConsolidados(vales, fecha, destino, empresa);
            String filename = "Vale_consolidado_" + destino.name() + "_" + fecha + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            log.error("Error al generar PDF consolidado de vales para {} / {}: {}", fecha, destino, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/vales")
    public ResponseEntity<List<SalidaDto>> listarValesPorFecha(@RequestParam LocalDate fecha) {
        return ResponseEntity.ok(salidaService.findValesActivosPorFecha(fecha));
    }

    @PostMapping("/vales/consolidado")
    public ResponseEntity<byte[]> descargarValesConsolidadosPorDestino(@RequestBody ValesConsolidadosRequest request) {
        if (request.getFecha() == null || request.getSalidaIds() == null || request.getSalidaIds().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            List<SalidaDto> valesDelDia = salidaService.findValesActivosPorFecha(request.getFecha());
            Set<UUID> seleccionados = Set.copyOf(request.getSalidaIds());
            Map<UUID, SalidaDto> porId = valesDelDia.stream()
                    .collect(Collectors.toMap(SalidaDto::getId, salida -> salida));
            if (seleccionados.size() != request.getSalidaIds().size() || !porId.keySet().containsAll(seleccionados)) {
                return ResponseEntity.badRequest().build();
            }
            List<SalidaDto> vales = valesDelDia.stream()
                    .filter(salida -> seleccionados.contains(salida.getId()))
                    .collect(Collectors.toList());

            ConfiguracionEmpresaDto empresa = configuracionEmpresaService.findActive()
                    .orElse(ConfiguracionEmpresaDto.builder()
                            .nombre("El Coloso S.A.")
                            .codigo("")
                            .nit("")
                            .direccion("Delicias")
                            .municipio("Puerto Padre")
                            .provincia("Las Tunas")
                            .build());
            byte[] pdfBytes = facturaPdfService.generarValesConsolidadosPorDestino(vales, request.getFecha(), empresa);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Vales_consolidados_" + request.getFecha() + ".pdf");
            headers.setContentLength(pdfBytes.length);
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            log.error("Error al generar PDF agrupado de vales para {}: {}", request.getFecha(), e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
