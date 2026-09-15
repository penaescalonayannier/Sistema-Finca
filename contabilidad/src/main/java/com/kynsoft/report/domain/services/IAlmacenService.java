package com.kynsoft.report.domain.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.AlmacenDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IAlmacenService {

    UUID create(AlmacenDto object);

    void update(AlmacenDto object);

    void delete(UUID id);

    void reactivar(UUID id);

    void establecerPrincipal(UUID id);

    AlmacenDto findById(UUID id);

    AlmacenDto findByInventario(String inventario);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    PaginatedResponse findByFincaId(UUID fincaId, Pageable pageable);

    void addProducto(UUID almacenId, UUID fincaProductoId);

    void removeProducto(UUID almacenId, UUID fincaProductoId);

    String generateInventarioCode(UUID fincaId);

    boolean existsByNombreAndFincaId(String nombre, UUID fincaId);

    boolean existsByNombreAndFincaIdAndIdNot(String nombre, UUID fincaId, UUID id);
}
