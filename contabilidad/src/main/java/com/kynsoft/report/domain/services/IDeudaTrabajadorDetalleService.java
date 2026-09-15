package com.kynsoft.report.domain.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.DeudaTrabajadorDetalleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IDeudaTrabajadorDetalleService {

    UUID create(DeudaTrabajadorDetalleDto dto);

    void registrar(DeudaTrabajadorDetalleDto dto);

    void desactivarBySalidaId(UUID salidaId);

    List<DeudaTrabajadorDetalleDto> findByTrabajadorId(UUID trabajadorId);

    // Obtener compras no pagadas ordenadas por fecha (FIFO)
    List<DeudaTrabajadorDetalleDto> findComprasNoPagadasByTrabajadorId(UUID trabajadorId);

    // Marcar un detalle como pagado
    void marcarComoPagado(UUID detalleId);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
