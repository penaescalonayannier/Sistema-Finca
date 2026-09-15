package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.domain.dto.DeudaTrabajadorDetalleDto;
import com.kynsoft.report.domain.services.IDeudaTrabajadorDetalleService;
import com.kynsoft.report.infrastructure.entity.DeudaTrabajadorDetalle;
import com.kynsoft.report.infrastructure.repository.command.DeudaTrabajadorDetalleWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.DeudaTrabajadorDetalleReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeudaTrabajadorDetalleServiceImpl implements IDeudaTrabajadorDetalleService {

    private final DeudaTrabajadorDetalleWriteDataJPARepository repositoryCommand;
    private final DeudaTrabajadorDetalleReadDataJPARepository repositoryQuery;

    public DeudaTrabajadorDetalleServiceImpl(
            DeudaTrabajadorDetalleWriteDataJPARepository repositoryCommand,
            DeudaTrabajadorDetalleReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(DeudaTrabajadorDetalleDto dto) {
        DeudaTrabajadorDetalle entity = new DeudaTrabajadorDetalle(dto);
        DeudaTrabajadorDetalle saved = repositoryCommand.save(entity);
        return saved.getId();
    }

    @Override
    public void registrar(DeudaTrabajadorDetalleDto dto) {
        DeudaTrabajadorDetalle entity = new DeudaTrabajadorDetalle(dto);
        repositoryCommand.save(entity);
    }

    @Override
    public void desactivarBySalidaId(UUID salidaId) {
        repositoryCommand.desactivarBySalidaId(salidaId);
    }

    @Override
    public List<DeudaTrabajadorDetalleDto> findByTrabajadorId(UUID trabajadorId) {
        return repositoryQuery.findByTrabajadorIdAndActivoTrue(trabajadorId)
                .stream()
                .map(DeudaTrabajadorDetalle::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeudaTrabajadorDetalleDto> findComprasNoPagadasByTrabajadorId(UUID trabajadorId) {
        return repositoryQuery.findComprasNoPagadasByTrabajadorId(trabajadorId)
                .stream()
                .map(DeudaTrabajadorDetalle::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public void marcarComoPagado(UUID detalleId) {
        DeudaTrabajadorDetalle detalle = repositoryQuery.findById(detalleId)
                .orElseThrow(() -> new RuntimeException("Detalle de deuda no encontrado: " + detalleId));
        detalle.setPagado(true);
        repositoryCommand.save(detalle);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<DeudaTrabajadorDetalle> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<DeudaTrabajadorDetalle> data = repositoryQuery.findAll(specifications, pageable);

        List<DeudaTrabajadorDetalleDto> responses = data.getContent().stream()
                .map(DeudaTrabajadorDetalle::toAggregate)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
