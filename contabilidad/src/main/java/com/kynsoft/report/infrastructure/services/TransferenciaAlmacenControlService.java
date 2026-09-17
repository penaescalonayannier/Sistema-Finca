package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.*;
import com.kynsoft.report.domain.services.INumeracionService;
import com.kynsoft.report.domain.services.IRegistroFormasNumeradasService;
import com.kynsoft.report.domain.services.IMovimientoStockService;
import com.kynsoft.report.infrastructure.entity.*;
import com.kynsoft.report.infrastructure.repository.command.*;
import com.kynsoft.report.infrastructure.repository.query.*;
import com.kynsoft.report.infrastructure.security.TenantContext;
import com.kynsoft.report.infrastructure.security.TenantValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Flujo controlado SC-2-09: despacho, recepción y cierre documentado. */
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "writeTransactionManager")
public class TransferenciaAlmacenControlService {
    private final AlmacenFincaProductoWriteDataJPARepository afpWrite;
    private final AlmacenFincaProductoReadDataJPARepository afpRead;
    private final AlmacenReadDataJPARepository almacenRead;
    private final FincaProductoReadDataJPARepository fincaProductoRead;
    private final TransferenciaAlmacenWriteDataJPARepository transferenciaWrite;
    private final TransferenciaAlmacenLineaWriteDataJPARepository lineaWrite;
    private final TransferenciaAlmacenReadDataJPARepository transferenciaRead;
    private final TransferenciaAlmacenLineaReadDataJPARepository lineaRead;
    private final INumeracionService numeracionService;
    private final IRegistroFormasNumeradasService registroFormasNumeradasService;
    private final IMovimientoStockService movimientoStockService;
    private final AuditoriaTransaccionalService auditoria;

    public UUID despachar(UUID origenAfpId, UUID destinoAlmacenId, Double cantidad, String observaciones) {
        if (cantidad == null || cantidad <= 0) throw new IllegalArgumentException("La cantidad a transferir debe ser mayor que cero.");
        if (texto(observaciones) == null) throw new IllegalArgumentException("Indique el motivo o referencia de la transferencia.");
        AlmacenFincaProducto origen = afpRead.findById(origenAfpId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado en almacén origen."));
        Almacen origenAlmacen = origen.getAlmacen();
        Almacen destino = almacenRead.findById(destinoAlmacenId)
                .orElseThrow(() -> new IllegalArgumentException("Almacén destino no encontrado."));
        UUID fincaId = origenAlmacen.getFinca().getId();
        TenantValidator.validateWriteAccess(fincaId);
        if (!Boolean.TRUE.equals(origen.getActivo()) || !Boolean.TRUE.equals(origenAlmacen.getActivo()) || !Boolean.TRUE.equals(destino.getActivo()))
            throw new IllegalArgumentException("Los almacenes y el producto deben estar activos.");
        if (origenAlmacen.getId().equals(destino.getId())) throw new IllegalArgumentException("El almacén destino debe ser diferente al origen.");
        if (!fincaId.equals(destino.getFinca().getId())) throw new IllegalArgumentException("La transferencia solo puede realizarse dentro de la misma finca.");
        double disponible = valor(origen.getStock());
        if (disponible + 0.0000001d < cantidad) throw new IllegalArgumentException("Stock insuficiente. Disponible: " + disponible);

        TransferenciaAlmacen t = new TransferenciaAlmacen();
        t.setId(UUID.randomUUID()); t.setFincaId(fincaId);
        t.setNumeroDocumento(registroFormasNumeradasService.emitir(new EmitirFormaNumeradaRequest(
                "TRANSFERENCIA_ALMACEN", AlcanceFormaNumerada.FINCA, fincaId, java.time.LocalDate.now(),
                "TRANSFERENCIA_ALMACEN", t.getId(), TenantContext.getUsuarioId())).getNumeroFormateado());
        t.setOrigenAlmacenId(origenAlmacen.getId()); t.setDestinoAlmacenId(destino.getId());
        t.setEstado(EstadoTransferenciaAlmacen.EN_TRANSITO); t.setObservaciones(texto(observaciones));
        t.setDespachadoPorId(TenantContext.getUsuarioId()); transferenciaWrite.save(t);
        TransferenciaAlmacenLinea l = new TransferenciaAlmacenLinea();
        l.setId(UUID.randomUUID()); l.setTransferenciaId(t.getId()); l.setFincaProductoId(origen.getFincaProducto().getId());
        l.setOrigenAlmacenFincaProductoId(origen.getId()); l.setCantidadDespachada(cantidad); lineaWrite.save(l);
        origen.setStock(disponible - cantidad); afpWrite.save(origen);
        movimiento(origen, TipoMovimientoStock.TRANSFERENCIA_SALIDA, cantidad, disponible, disponible - cantidad, t,
                "Despacho SC-2-09 " + t.getNumeroDocumento() + " hacia " + destino.getNombre());
        auditar(TipoAccion.TRANSFER, t, "Despacho de transferencia SC-2-09", Map.of("estado", "N/A"),
                Map.of("estado", t.getEstado(), "numero", t.getNumeroDocumento(), "cantidad", cantidad));
        return t.getId();
    }

    public TransferenciaAlmacenDetalleDto recibir(UUID transferenciaId, UUID almacenDestinoId, RecepcionTransferenciaAlmacenRequest request) {
        TransferenciaAlmacen t = transferenciaWrite.findByIdForUpdate(transferenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Transferencia no encontrada."));
        TenantValidator.validateWriteAccess(t.getFincaId());
        if (!t.getDestinoAlmacenId().equals(almacenDestinoId)) throw new IllegalArgumentException("La transferencia no pertenece a este almacén destino.");
        if (t.getEstado() != EstadoTransferenciaAlmacen.EN_TRANSITO) throw new IllegalArgumentException("Solo se pueden recibir transferencias en tránsito.");
        if (request == null || request.getLineas() == null || request.getLineas().isEmpty()) throw new IllegalArgumentException("Indique las cantidades recibidas.");
        Map<UUID, RecepcionTransferenciaAlmacenRequest.Linea> recibidas = request.getLineas().stream()
                .filter(Objects::nonNull).filter(x -> x.getLineaId() != null)
                .collect(Collectors.toMap(RecepcionTransferenciaAlmacenRequest.Linea::getLineaId, Function.identity(), (a,b) -> a));
        List<TransferenciaAlmacenLinea> lineas = lineaRead.findByTransferenciaId(t.getId());
        if (recibidas.size() != lineas.size() || lineas.stream().anyMatch(x -> !recibidas.containsKey(x.getId())))
            throw new IllegalArgumentException("La recepción debe confirmar cada renglón del documento.");
        Almacen destino = almacenRead.findById(t.getDestinoAlmacenId()).orElseThrow(() -> new IllegalArgumentException("Almacén destino no encontrado."));
        boolean todoRechazado = true;
        for (TransferenciaAlmacenLinea l : lineas) {
            double despachada = valor(l.getCantidadDespachada());
            double recibida = valor(recibidas.get(l.getId()).getCantidadRecibida());
            if (recibida < 0 || recibida - despachada > 0.0000001d) throw new IllegalArgumentException("La cantidad recibida no puede exceder la despachada.");
            double rechazada = despachada - recibida;
            if (rechazada > 0 && texto(recibidas.get(l.getId()).getObservaciones()) == null && texto(request.getObservaciones()) == null)
                throw new IllegalArgumentException("Explique la diferencia o rechazo en cada renglón observado.");
            if (recibida > 0) { acreditarDestino(t, destino, l, recibida); todoRechazado = false; }
            if (rechazada > 0) reintegrarOrigen(t, l, rechazada, "Rechazo/diferencia al recibir SC-2-09");
            l.setCantidadRecibida(recibida); l.setCantidadRechazada(rechazada); l.setObservaciones(texto(recibidas.get(l.getId()).getObservaciones())); lineaWrite.save(l);
        }
        t.setEstado(todoRechazado ? EstadoTransferenciaAlmacen.RECHAZADA : EstadoTransferenciaAlmacen.RECIBIDA);
        t.setFechaRecepcion(LocalDateTime.now()); t.setRecibidoPorId(TenantContext.getUsuarioId()); t.setMotivoCierre(texto(request.getObservaciones())); transferenciaWrite.save(t);
        auditar(TipoAccion.TRANSFER, t, "Recepción/cierre de transferencia SC-2-09", Map.of("estado", EstadoTransferenciaAlmacen.EN_TRANSITO),
                Map.of("estado", t.getEstado(), "observaciones", Objects.toString(texto(request.getObservaciones()), "")));
        return detalle(t);
    }

    public void revertir(UUID transferenciaId, UUID almacenOrigenId, String motivo) {
        TransferenciaAlmacen t = transferenciaWrite.findByIdForUpdate(transferenciaId).orElseThrow(() -> new IllegalArgumentException("Transferencia no encontrada."));
        TenantValidator.validateWriteAccess(t.getFincaId());
        if (!t.getOrigenAlmacenId().equals(almacenOrigenId)) throw new IllegalArgumentException("La transferencia no pertenece a este almacén origen.");
        if (t.getEstado() != EstadoTransferenciaAlmacen.EN_TRANSITO) throw new IllegalArgumentException("Solo se puede revertir una transferencia aún no recibida.");
        if (texto(motivo) == null) throw new IllegalArgumentException("El motivo de reversión es obligatorio.");
        for (TransferenciaAlmacenLinea l : lineaRead.findByTransferenciaId(t.getId())) reintegrarOrigen(t, l, valor(l.getCantidadDespachada()), "Reversión SC-2-09: " + texto(motivo));
        t.setEstado(EstadoTransferenciaAlmacen.REVERSADA); t.setFechaRecepcion(LocalDateTime.now()); t.setRecibidoPorId(TenantContext.getUsuarioId()); t.setMotivoCierre(texto(motivo)); transferenciaWrite.save(t);
        auditar(TipoAccion.TRANSFER, t, "Reversión de transferencia SC-2-09", Map.of("estado", EstadoTransferenciaAlmacen.EN_TRANSITO), Map.of("estado", t.getEstado(), "motivo", texto(motivo)));
    }

    @Transactional(readOnly = true, transactionManager = "readTransactionManager")
    public List<TransferenciaAlmacenDetalleDto> pendientes(UUID destinoAlmacenId) {
        Almacen a = almacenRead.findById(destinoAlmacenId).orElseThrow(() -> new IllegalArgumentException("Almacén no encontrado."));
        TenantValidator.validateReadAccess(a.getFinca().getId());
        return transferenciaRead.findByDestinoAlmacenIdAndEstadoOrderByFechaDespachoAsc(destinoAlmacenId, EstadoTransferenciaAlmacen.EN_TRANSITO).stream().map(this::detalle).toList();
    }

    @Transactional(readOnly = true, transactionManager = "readTransactionManager")
    public List<TransferenciaAlmacenDetalleDto> listar(UUID fincaId) {
        TenantValidator.validateReadAccess(fincaId);
        return transferenciaRead.findByFincaIdOrderByFechaDespachoDesc(fincaId).stream().map(this::detalle).toList();
    }

    private void acreditarDestino(TransferenciaAlmacen t, Almacen destino, TransferenciaAlmacenLinea l, double cantidad) {
        AlmacenFincaProducto afp = afpRead.findByAlmacenIdAndFincaProductoIdAndActivoTrue(destino.getId(), l.getFincaProductoId()).orElseGet(() -> {
            FincaProducto fp = fincaProductoRead.findById(l.getFincaProductoId()).orElseThrow(() -> new IllegalArgumentException("Producto de finca no encontrado."));
            AlmacenFincaProducto n = new AlmacenFincaProducto(); n.setId(UUID.randomUUID()); n.setAlmacen(destino); n.setFincaProducto(fp); n.setActivo(true); n.setStock(0d); n.setStockMinimo(0d); return afpWrite.save(n);
        });
        double anterior = valor(afp.getStock()); afp.setStock(anterior + cantidad); afpWrite.save(afp); l.setDestinoAlmacenFincaProductoId(afp.getId());
        movimiento(afp, TipoMovimientoStock.TRANSFERENCIA_ENTRADA, cantidad, anterior, anterior + cantidad, t, "Recepción SC-2-09 " + t.getNumeroDocumento());
    }

    private void reintegrarOrigen(TransferenciaAlmacen t, TransferenciaAlmacenLinea l, double cantidad, String motivo) {
        AlmacenFincaProducto afp = afpRead.findById(l.getOrigenAlmacenFincaProductoId()).orElseThrow(() -> new IllegalArgumentException("Producto origen no encontrado."));
        double anterior = valor(afp.getStock()); afp.setStock(anterior + cantidad); afpWrite.save(afp);
        movimiento(afp, TipoMovimientoStock.TRANSFERENCIA_ENTRADA, cantidad, anterior, anterior + cantidad, t, motivo + " " + t.getNumeroDocumento());
    }

    private void movimiento(AlmacenFincaProducto afp, TipoMovimientoStock tipo, double cantidad, double anterior, double nuevo, TransferenciaAlmacen t, String descripcion) {
        movimientoStockService.registrar(MovimientoStockDto.builder().id(UUID.randomUUID()).fincaProductoId(afp.getFincaProducto().getId())
                .fincaId(t.getFincaId()).productoId(afp.getFincaProducto().getProducto().getId()).almacenId(afp.getAlmacen().getId()).tipo(tipo)
                .cantidad(cantidad).stockAnterior(anterior).stockNuevo(nuevo).referenciaId(t.getId()).referenciaTabla("transferencia_almacen")
                .descripcion(descripcion).observaciones(t.getObservaciones()).build());
    }
    private TransferenciaAlmacenDetalleDto detalle(TransferenciaAlmacen t) {
        Almacen o = almacenRead.findById(t.getOrigenAlmacenId()).orElse(null), d = almacenRead.findById(t.getDestinoAlmacenId()).orElse(null);
        return TransferenciaAlmacenDetalleDto.builder().id(t.getId()).fincaId(t.getFincaId()).numeroDocumento(t.getNumeroDocumento())
                .origenAlmacenId(t.getOrigenAlmacenId()).origenAlmacenNombre(o == null ? null : o.getNombre()).destinoAlmacenId(t.getDestinoAlmacenId()).destinoAlmacenNombre(d == null ? null : d.getNombre())
                .estado(t.getEstado()).fechaDespacho(t.getFechaDespacho()).fechaRecepcion(t.getFechaRecepcion()).observaciones(t.getObservaciones()).motivoCierre(t.getMotivoCierre())
                .lineas(lineaRead.findByTransferenciaId(t.getId()).stream().map(l -> { FincaProducto fp = fincaProductoRead.findById(l.getFincaProductoId()).orElse(null); return TransferenciaAlmacenDetalleDto.Linea.builder().id(l.getId()).fincaProductoId(l.getFincaProductoId()).productoNombre(fp == null || fp.getProducto() == null ? "Producto" : fp.getProducto().getName()).unidadMedida(fp == null || fp.getProducto() == null || fp.getProducto().getUnidadMedida() == null ? null : fp.getProducto().getUnidadMedida().getNombre()).cantidadDespachada(l.getCantidadDespachada()).cantidadRecibida(l.getCantidadRecibida()).cantidadRechazada(l.getCantidadRechazada()).observaciones(l.getObservaciones()).build(); }).toList()).build();
    }
    private void auditar(TipoAccion a, TransferenciaAlmacen t, String d, Object antes, Object despues) { auditoria.registrarDespuesDeConfirmar(a, "TRANSFERENCIA_ALMACEN", t.getId(), d, antes, despues); }
    private static double valor(Double x) { return x == null ? 0d : x; }
    private static String texto(String x) { return x == null || x.trim().isEmpty() ? null : x.trim(); }
}
