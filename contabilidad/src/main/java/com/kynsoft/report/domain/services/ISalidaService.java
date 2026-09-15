package com.kynsoft.report.domain.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.ItemSalidaDto;
import com.kynsoft.report.domain.dto.DestinoSalida;
import com.kynsoft.report.domain.dto.SalidaDto;
import com.kynsoft.report.domain.dto.TipoSalida;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

public interface ISalidaService {

    UUID create(SalidaDto dto, List<ItemSalidaDto> items);

    void update(SalidaDto dto, List<ItemSalidaDto> items);

    void delete(UUID id);

    SalidaDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<SalidaDto> findValesActivosPorFechaYDestino(LocalDate fecha, DestinoSalida destino);

    String generarNumero(TipoSalida tipo);
}
