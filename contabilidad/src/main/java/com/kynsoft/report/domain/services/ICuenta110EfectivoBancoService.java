package com.kynsoft.report.domain.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.Cuenta110EfectivoBancoDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ICuenta110EfectivoBancoService {

    void create(Cuenta110EfectivoBancoDto object);

    void update(Cuenta110EfectivoBancoDto object);

    void delete(UUID id);

    void deleteIds(List<UUID> ids);

    Cuenta110EfectivoBancoDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Cuenta110EfectivoBancoDto findUnique(); // <-- NUEVO MÉTODO
}
