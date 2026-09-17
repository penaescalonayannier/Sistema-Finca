package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.applications.command.almacenproducto.entrada.EntradaAlmacenCommand;
import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.dto.InformeRecepcionDto;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.dto.TipoDocumento;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.domain.services.IAlmacenFincaProductoService;
import com.kynsoft.report.domain.services.INumeracionService;
import com.kynsoft.report.infrastructure.entity.InformeRecepcion;
import com.kynsoft.report.infrastructure.entity.InformeRecepcionLinea;
import com.kynsoft.report.infrastructure.repository.command.InformeRecepcionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.InformeRecepcionReadDataJPARepository;
import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.response.ErrorField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

/** Registra el informe SC-2-04 y su movimiento físico en una única transacción. */
@Service @RequiredArgsConstructor
public class InformeRecepcionService {
    private final InformeRecepcionWriteDataJPARepository writeRepository;
    private final InformeRecepcionReadDataJPARepository readRepository;
    private final IAlmacenFincaProductoService almacenProductoService;
    private final INumeracionService numeracionService;
    private final AuditoriaTransaccionalService auditoria;

    @Transactional(transactionManager = "writeTransactionManager")
    public Resultado registrar(EntradaAlmacenCommand command) {
        TipoMovimientoStock tipo = command.getTipo();
        if (tipo != TipoMovimientoStock.ENTRADA_FACTURA && tipo != TipoMovimientoStock.ENTRADA_CONDUCE) {
            throw error("tipo", "El informe de recepción solo respalda facturas o conduce.");
        }
        validarTexto(command.getProveedor(), "proveedor", "Debe indicar el proveedor/remitente.");
        validarTexto(command.getResponsableEntrega(), "responsableEntrega", "Debe indicar quien entrega.");
        validarTexto(command.getResponsableRecibe(), "responsableRecibe", "Debe indicar quien recibe en el almacén.");
        if (command.getCostoUnitario() == null || !Double.isFinite(command.getCostoUnitario()) || command.getCostoUnitario() < 0) {
            throw error("costoUnitario", "Debe indicar un costo unitario válido.");
        }
        String numeroFuente = tipo == TipoMovimientoStock.ENTRADA_FACTURA ? command.getNumeroFactura() : command.getNumeroConduce();
        validarTexto(numeroFuente, tipo == TipoMovimientoStock.ENTRADA_FACTURA ? "numeroFactura" : "numeroConduce",
                "Debe indicar el número del documento fuente.");
        if (command.getCantidad() == null || !Double.isFinite(command.getCantidad()) || command.getCantidad() <= 0) {
            throw error("cantidad", "La cantidad debe ser mayor que cero.");
        }
        AlmacenFincaProductoDto productoAlmacen = almacenProductoService.findById(command.getAlmacenFincaProductoId());
        if (productoAlmacen.getAlmacenId() == null || productoAlmacen.getFincaProductoId() == null || productoAlmacen.getProductoId() == null) {
            throw error("almacenFincaProductoId", "El producto del almacén no tiene una trazabilidad válida.");
        }
        // La finca se obtiene del producto en almacén, no desde la petición del cliente.
        UUID fincaId = productoAlmacen.getFincaId();
        if (fincaId == null) throw error("almacenFincaProductoId", "No se pudo determinar la finca del almacén.");
        UUID informeId = UUID.randomUUID();
        UUID movimientoId = UUID.randomUUID();
        InformeRecepcion informe = new InformeRecepcion();
        informe.setId(informeId); informe.setFincaId(fincaId); informe.setAlmacenId(productoAlmacen.getAlmacenId());
        informe.setNumeroDocumento(numeracionService.generarSiguienteNumero(fincaId, TipoDocumento.RECEPCION));
        informe.setTipoFuente(tipo); informe.setNumeroFuente(numeroFuente.trim());
        informe.setFechaDocumento(command.getFechaDocumento() == null ? LocalDate.now() : command.getFechaDocumento());
        informe.setProveedor(command.getProveedor().trim()); informe.setResponsableEntrega(command.getResponsableEntrega().trim());
        informe.setResponsableRecibe(command.getResponsableRecibe().trim()); informe.setObservaciones(limpiar(command.getDescripcion()));
        informe.setEstado("REGISTRADO"); informe.setMovimientoStockId(movimientoId);
        // El movimiento usa el UUID del informe como referencia, aun antes de
        // insertar la cabecera. La transacción única revierte ambos si falla
        // cualquiera de los dos; la FK inversa exige guardar la cabecera luego
        // de crear el movimiento.
        String descripcion = "SC-2-04 " + informe.getNumeroDocumento() + " | "
                + (tipo == TipoMovimientoStock.ENTRADA_FACTURA ? "Factura " : "Conduce ") + numeroFuente.trim();
        almacenProductoService.entradaConInformeRecepcion(command.getAlmacenFincaProductoId(), command.getCantidad(),
                tipo, informeId, movimientoId, descripcion);
        AlmacenFincaProductoDto saldo = almacenProductoService.findById(command.getAlmacenFincaProductoId());
        InformeRecepcionLinea linea = new InformeRecepcionLinea();
        linea.setAlmacenFincaProductoId(command.getAlmacenFincaProductoId()); linea.setFincaProductoId(productoAlmacen.getFincaProductoId());
        linea.setProductoId(productoAlmacen.getProductoId()); linea.setProductoCodigo(nvl(productoAlmacen.getProductoCode()));
        linea.setProductoNombre(nvl(productoAlmacen.getProductoName()));
        linea.setUnidadMedida(productoAlmacen.getUnidadMedida() == null ? null : productoAlmacen.getUnidadMedida().toString());
        linea.setCantidad(command.getCantidad()); linea.setCostoUnitario(command.getCostoUnitario());
        linea.setImporte(command.getCantidad() * command.getCostoUnitario()); linea.setSaldoPosterior(saldo.getStock());
        informe.agregarLinea(linea); writeRepository.save(informe);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.CREATE, "INFORME_RECEPCION", informeId,
                "Informe de recepción SC-2-04 " + informe.getNumeroDocumento(), null,
                Map.of("numero", informe.getNumeroDocumento(), "movimientoStockId", movimientoId,
                        "fuente", numeroFuente.trim(), "importe", linea.getImporte()));
        return new Resultado(informeId, movimientoId, saldo.getStock());
    }

    @Transactional(readOnly = true, transactionManager = "readTransactionManager")
    public InformeRecepcionDto obtener(UUID id) { return aDto(readRepository.findDetalleById(id).orElseThrow(() -> error("id", "Informe de recepción no encontrado."))); }
    private InformeRecepcionDto aDto(InformeRecepcion i) { return InformeRecepcionDto.builder().id(i.getId()).fincaId(i.getFincaId()).almacenId(i.getAlmacenId()).numeroDocumento(i.getNumeroDocumento()).tipoFuente(i.getTipoFuente()).numeroFuente(i.getNumeroFuente()).fechaDocumento(i.getFechaDocumento()).proveedor(i.getProveedor()).responsableEntrega(i.getResponsableEntrega()).responsableRecibe(i.getResponsableRecibe()).observaciones(i.getObservaciones()).estado(i.getEstado()).movimientoStockId(i.getMovimientoStockId()).createdAt(i.getCreatedAt()).lineas(i.getLineas().stream().map(l -> InformeRecepcionDto.Linea.builder().almacenFincaProductoId(l.getAlmacenFincaProductoId()).fincaProductoId(l.getFincaProductoId()).productoId(l.getProductoId()).productoCodigo(l.getProductoCodigo()).productoNombre(l.getProductoNombre()).unidadMedida(l.getUnidadMedida()).cantidad(l.getCantidad()).costoUnitario(l.getCostoUnitario()).importe(l.getImporte()).saldoPosterior(l.getSaldoPosterior()).build()).toList()).build(); }
    private void validarTexto(String value, String field, String message) { if (value == null || value.trim().isEmpty()) throw error(field, message); }
    private String limpiar(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private String nvl(String value) { return value == null ? "No registrado" : value; }
    private BusinessNotFoundException error(String field, String message) { return new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_NOT_FOUND, new ErrorField(field, message))); }
    public record Resultado(UUID informeId, UUID movimientoId, Double stockNuevo) { }
}
