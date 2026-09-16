package com.kynsoft.report.controller;
import com.kynsoft.report.domain.dto.*;
import com.kynsoft.report.domain.services.ICajaOficialService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/caja-oficial")
public class CajaOficialController {
 private final ICajaOficialService service; public CajaOficialController(ICajaOficialService service){this.service=service;}
 @PostMapping("/fondos") public ResponseEntity<FondoCajaDto> guardarFondo(@RequestBody FondoCajaRequest r){return ResponseEntity.ok(service.guardarFondo(r));}
 @GetMapping("/fondos") public ResponseEntity<List<FondoCajaDto>> fondos(@RequestParam UUID fincaId){return ResponseEntity.ok(service.listarFondos(fincaId));}
 @PostMapping("/responsabilidades") public ResponseEntity<Map<String,UUID>> crearActa(@RequestBody ActaResponsabilidadCajaRequest r){return ResponseEntity.ok(Map.of("id",service.crearActa(r)));}
 @PutMapping("/responsabilidades/{id}/cerrar") public ResponseEntity<Void> cerrarActa(@PathVariable UUID id,@RequestBody(required=false) Map<String,String> r){service.cerrarActa(id,r==null?null:r.get("observaciones"));return ResponseEntity.noContent().build();}
 @GetMapping("/responsabilidades") public ResponseEntity<List<ActaResponsabilidadCajaDto>> actas(@RequestParam UUID fincaId){return ResponseEntity.ok(service.listarActas(fincaId));}
 @PostMapping("/incidencias-arqueo") public ResponseEntity<Map<String,UUID>> crearIncidencia(@RequestBody CrearIncidenciaArqueoCajaRequest r){return ResponseEntity.ok(Map.of("id",service.crearIncidencia(r)));}
 @PutMapping("/incidencias-arqueo/{id}") public ResponseEntity<Void> actualizarIncidencia(@PathVariable UUID id,@RequestBody ActualizarIncidenciaArqueoCajaRequest r){service.actualizarIncidencia(id,r);return ResponseEntity.noContent().build();}
 @GetMapping("/incidencias-arqueo") public ResponseEntity<List<IncidenciaArqueoCajaDto>> incidencias(@RequestParam UUID fincaId){return ResponseEntity.ok(service.listarIncidencias(fincaId));}
 @GetMapping("/tablero") public ResponseEntity<TableroControlCajaDto> tablero(@RequestParam UUID fincaId,@RequestParam int anio,@RequestParam int mes){return ResponseEntity.ok(service.tablero(fincaId,anio,mes));}
}
