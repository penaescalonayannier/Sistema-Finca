package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.PagoDeudaDto;

import java.util.List;
import java.util.UUID;

public interface IPagoDeudaService {

    UUID registrarPago(PagoDeudaDto dto);

    PagoDeudaDto findById(UUID id);

    List<PagoDeudaDto> findByTrabajadorId(UUID trabajadorId);
}
