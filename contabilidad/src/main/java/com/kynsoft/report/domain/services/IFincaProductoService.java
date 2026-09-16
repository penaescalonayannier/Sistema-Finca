package com.kynsoft.report.domain.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.EstadoStock;
import com.kynsoft.report.domain.dto.FincaProductoDto;
import com.kynsoft.report.domain.dto.ResumenAlertasDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IFincaProductoService {

    // Asignar producto a finca con stock inicial y stockMinimo
    UUID asignarProductoAFinca(UUID fincaId, UUID productoId, Double stock, Double stockMinimo);


    // Actualizar configuración (stockMinimo)
    void actualizarConfiguracion(UUID id, Double stockMinimo);

    // Obtener por ID
    FincaProductoDto getById(UUID id);

    void actualizarStock(UUID fincaId, UUID productoId, Double stock);


    void removerProductoDeFinca(UUID fincaId, UUID productoId);

    void removerTodosProductosDeFinca(UUID fincaId);

    List<FincaProductoDto> obtenerProductosDeFinca(UUID fincaId);

    List<FincaProductoDto> obtenerFincasDeProducto(UUID productoId);

    FincaProductoDto obtenerRelacion(UUID fincaId, UUID productoId);

    Double obtenerStock(UUID fincaId, UUID productoId);

    // Entrada de producción: suma cantidad al stock existente
    void entradaProduccion(UUID fincaId, UUID productoId, Double cantidad, String descripcion);

    // Entrada de producción con centro de costo para contabilidad
    void entradaProduccion(UUID fincaId, UUID productoId, Double cantidad, String descripcion, String centroCosto);

    // Entrada de producción con referencia para auditoría
    void entradaProduccion(UUID fincaId, UUID productoId, Double cantidad, String descripcion, UUID referenciaId);

    // Entrada por factura: requiere número de factura
    void entradaFactura(UUID id, Integer cantidad, String numeroFactura, String observaciones);

    // Entrada por conduce: sin referencia formal
    void entradaConduce(UUID id, Integer cantidad, String observaciones);

    // Ajuste manual de stock: requiere observaciones
    void ajusteManual(UUID id, Double cantidad, String observaciones);

    // Salida/Reverso de producción: resta cantidad del stock existente
    void decrementarStock(UUID fincaId, UUID productoId, Double cantidad);

    // Decremento con tipo y referencia para auditoría
    void decrementarStock(UUID fincaId, UUID productoId, Double cantidad,
                          TipoMovimientoStock tipo, UUID referenciaId, String referenciaTabla);

    // Búsqueda paginada con filtros
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    // Obtener productos con alerta de stock bajo
    PaginatedResponse getAlertasStockBajo(Pageable pageable);

    // Obtener resumen de alertas con filtros
    ResumenAlertasDto getResumenAlertas(UUID fincaId, EstadoStock estado, int limit);

    // Actualizar stock mínimo y máximo
    FincaProductoDto actualizarStockMinMax(UUID id, Double stockMinimo, Double stockMaximo);
}
