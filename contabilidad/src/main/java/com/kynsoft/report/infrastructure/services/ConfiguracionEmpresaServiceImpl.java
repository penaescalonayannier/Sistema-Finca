package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.ConfiguracionEmpresaDto;
import com.kynsoft.report.domain.services.IConfiguracionEmpresaService;
import com.kynsoft.report.infrastructure.entity.ConfiguracionEmpresa;
import com.kynsoft.report.infrastructure.repository.command.ConfiguracionEmpresaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ConfiguracionEmpresaReadDataJPARepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ConfiguracionEmpresaServiceImpl implements IConfiguracionEmpresaService {

    private final ConfiguracionEmpresaReadDataJPARepository readRepository;
    private final ConfiguracionEmpresaWriteDataJPARepository writeRepository;

    public ConfiguracionEmpresaServiceImpl(
            ConfiguracionEmpresaReadDataJPARepository readRepository,
            ConfiguracionEmpresaWriteDataJPARepository writeRepository) {
        this.readRepository = readRepository;
        this.writeRepository = writeRepository;
    }

    @Override
    public ConfiguracionEmpresaDto create(ConfiguracionEmpresaDto dto) {
        if (dto.getId() == null) {
            dto.setId(UUID.randomUUID());
        }
        ConfiguracionEmpresa entity = new ConfiguracionEmpresa(dto);
        entity = writeRepository.save(entity);
        return entity.toAggregate();
    }

    @Override
    public ConfiguracionEmpresaDto update(ConfiguracionEmpresaDto dto) {
        ConfiguracionEmpresa entity = new ConfiguracionEmpresa(dto);
        entity = writeRepository.save(entity);
        return entity.toAggregate();
    }

    @Override
    public void delete(UUID id) {
        writeRepository.deleteById(id);
    }

    @Override
    public Optional<ConfiguracionEmpresaDto> findById(UUID id) {
        return readRepository.findById(id)
                .map(ConfiguracionEmpresa::toAggregate);
    }

    @Override
    public Optional<ConfiguracionEmpresaDto> findActive() {
        return readRepository.findFirstByActivoTrueOrderByNombreAsc()
                .map(ConfiguracionEmpresa::toAggregate);
    }
}
