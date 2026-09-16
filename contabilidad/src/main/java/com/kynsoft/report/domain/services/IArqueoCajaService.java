package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.ArqueoCajaDetalleDto;
import com.kynsoft.report.domain.dto.ArqueoCajaResumenDto;
import com.kynsoft.report.domain.dto.CerrarArqueoCajaRequest;
import com.kynsoft.report.domain.dto.CrearArqueoCajaRequest;

import java.util.List;
import java.util.UUID;

public interface IArqueoCajaService {
    UUID crear(CrearArqueoCajaRequest request);
    void cerrar(UUID id, CerrarArqueoCajaRequest request);
    List<ArqueoCajaResumenDto> listar(UUID fincaId);
    ArqueoCajaDetalleDto detalle(UUID id);
}
