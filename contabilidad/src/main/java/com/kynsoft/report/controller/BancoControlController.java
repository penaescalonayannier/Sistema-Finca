package com.kynsoft.report.controller;
import com.kynsoft.report.domain.dto.*; import com.kynsoft.report.infrastructure.entity.*; import com.kynsoft.report.infrastructure.services.BancoControlService; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/banco") public class BancoControlController {
 private final BancoControlService service; public BancoControlController(BancoControlService service){this.service=service;}
 @PostMapping("/cheques-transferencias") public ResponseEntity<Map<String,UUID>> emitir(@RequestBody ChequeTransferenciaRequest request){return ResponseEntity.ok(Map.of("id",service.emitir(request)));}
 @GetMapping("/cheques-transferencias") public List<ChequeTransferencia> listarEmitidos(@RequestParam UUID fincaId){return service.listarEmitidos(fincaId);}
 @PostMapping("/cheques-transferencias/{id}/confirmar") public ResponseEntity<Void> confirmar(@PathVariable UUID id,@RequestBody Map<String,Object> body){service.confirmar(id,Boolean.TRUE.equals(body.get("cobrado")),(String)body.get("observaciones"));return ResponseEntity.noContent().build();}
 @PostMapping("/conciliaciones") public ResponseEntity<Map<String,UUID>> conciliar(@RequestBody CierreConciliacionBancariaRequest request){return ResponseEntity.ok(Map.of("id",service.registrarConciliacion(request)));}
 @GetMapping("/conciliaciones") public List<ConciliacionBancaria> listarConciliaciones(@RequestParam UUID fincaId){return service.listarConciliaciones(fincaId);}
 @GetMapping("/conciliaciones/{id}") public ConciliacionBancariaDetalleResponse detalleConciliacion(@PathVariable UUID id){return service.detalleConciliacion(id);}
 @PostMapping("/conciliaciones/{id}/cerrar") public ResponseEntity<Void> cerrarConciliacion(@PathVariable UUID id,@RequestBody(required=false) Map<String,String> body){service.cerrarConciliacion(id,body==null?null:body.get("observaciones"));return ResponseEntity.noContent().build();}
}
