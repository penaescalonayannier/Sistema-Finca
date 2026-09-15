package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.ConfiguracionEmpresaDto;

import java.util.Optional;
import java.util.UUID;

public interface IConfiguracionEmpresaService {

    ConfiguracionEmpresaDto create(ConfiguracionEmpresaDto dto);

    ConfiguracionEmpresaDto update(ConfiguracionEmpresaDto dto);

    void delete(UUID id);

    Optional<ConfiguracionEmpresaDto> findById(UUID id);

    Optional<ConfiguracionEmpresaDto> findActive();
}
