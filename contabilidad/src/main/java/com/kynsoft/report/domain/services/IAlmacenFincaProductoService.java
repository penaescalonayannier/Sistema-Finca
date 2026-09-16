package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IAlmacenFincaProductoService {

    // ==================== CRUD ====================

    UUID asignarProducto(UUID almacenId, UUID fincaProductoId, Double stockInicial,
                         Double stockMinimo, Double stockMaximo);

    void actualizarStock(UUID id, Double nuevoStock);

    void actualizarLimites(UUID id, Double stockMinimo, Double stockMaximo);

    void removerProducto(UUID id);

    void reactivarProducto(UUID id);

    // ==================== ENTRADAS ====================

    void entradaProduccion(UUID almacenFincaProductoId, Double cantidad, String descripcion);

    void entradaProduccion(UUID almacenFincaProductoId, Double cantidad, String descripcion, String centroCosto);

    void entradaFactura(UUID almacenFincaProductoId, Double cantidad, String numeroFactura, String descripcion);

    void entradaConduce(UUID almacenFincaProductoId, Double cantidad, String observaciones);

    void entrada(UUID almacenFincaProductoId, Double cantidad, TipoMovimientoStock tipo, String descripcion);

    void entrada(UUID almacenFincaProductoId, Double cantidad, TipoMovimientoStock tipo, String descripcion, String centroCosto);

    // ==================== SALIDAS ====================

    void salida(UUID almacenFincaProductoId, Double cantidad, String descripcion);

    void salidaTrabajador(UUID almacenFincaProductoId, Double cantidad, UUID trabajadorId, String descripcion);

    void salidaComedor(UUID almacenFincaProductoId, Double cantidad, String descripcion);

    // ==================== TRANSFERENCIAS ====================

    void transferir(UUID origenId, UUID destinoAlmacenId, Double cantidad, String observaciones);

    void transferirConDestino(UUID origenAlmacenId, UUID fincaProductoId,
                                      UUID destinoAlmacenId, Double cantidad, String observaciones);

    // ==================== QUERIES ====================

    AlmacenFincaProductoDto findById(UUID id);

    AlmacenFincaProductoDto findByAlmacenIdAndFincaProductoId(UUID almacenId, UUID fincaProductoId);

    List<AlmacenFincaProductoDto> findByAlmacenId(UUID almacenId);

    List<AlmacenFincaProductoDto> findByFincaProductoId(UUID fincaProductoId);

    List<AlmacenFincaProductoDto> findByFincaId(UUID fincaId);

    PaginatedResponse search(UUID almacenId, Pageable pageable, List<FilterCriteria> filterCriteria);

    // ==================== UTILIDADES ====================

    Double getStockTotalAlmacen(UUID almacenId);

    Double getStockTotalProductoEnFinca(UUID fincaProductoId);

    boolean existsProductoEnAlmacen(UUID almacenId, UUID fincaProductoId);

    List<AlmacenFincaProductoDto> getAlmacenesDestinoDisponibles(UUID almacenId, UUID fincaProductoId);
}
