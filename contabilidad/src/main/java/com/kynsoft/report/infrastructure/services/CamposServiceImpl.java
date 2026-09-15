package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.CamposResponse;
import com.kynsoft.report.domain.dto.CampoDto;
import com.kynsoft.report.domain.services.ICamposService;
import com.kynsoft.report.infrastructure.entity.Campo;
import com.kynsoft.report.infrastructure.repository.command.CamposWriteDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.kynsoft.report.infrastructure.repository.query.CampoReadDataJPARepository;

@Service
@Transactional
public class CamposServiceImpl implements ICamposService {

    private final CamposWriteDataJPARepository repositoryCommand;
    private final CampoReadDataJPARepository repositoryQuery;

    public CamposServiceImpl(CamposWriteDataJPARepository repositoryCommand,
                             CampoReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(CampoDto object) {
        repositoryCommand.save(new Campo(object));
    }

    @Override
    public void update(CampoDto object) {
        repositoryCommand.save(new Campo(object));
    }

    @Override
    public void delete(UUID id) {
        try {
            repositoryCommand.deleteById(id);
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", "Element cannot be deleted as it has a related element.")));
        }
    }

    @Override
    public CampoDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Campo::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Cliente not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Campo> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Campo> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Campo> data) {
        List<CamposResponse> responses = data.getContent().stream()
                .map(Campo::toAggregate)
                .map(CamposResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public int calcularDepreciacion(List<UUID> campoIds, Integer meses) {
        int actualizados = 0;
        List<Campo> campos = repositoryQuery.findAllById(campoIds);

        for (Campo campo : campos) {
            if (campo.getValorAdquisicion() != null && campo.getTasaDepreciacionAnual() != null) {
                Double valorBase = campo.getValorAdquisicion();
                Double valorResidual = campo.getValorResidual() != null ? campo.getValorResidual() : 0.0;
                Double tasaAnual = campo.getTasaDepreciacionAnual();

                // Calculate monthly depreciation
                // Formula: (Valor Adquisición - Valor Residual) * (Tasa Anual / 12 / 100) * meses
                Double depreciacionMensual = (valorBase - valorResidual) * (tasaAnual / 12.0 / 100.0);
                Double nuevaDepreciacion = depreciacionMensual * meses;

                // Update accumulated depreciation
                Double depreciacionActual = campo.getDepreciacionAcumulada() != null ? campo.getDepreciacionAcumulada() : 0.0;
                Double depreciacionTotal = depreciacionActual + nuevaDepreciacion;

                // Ensure depreciation doesn't exceed (valor adquisición - valor residual)
                Double maxDepreciacion = valorBase - valorResidual;
                if (depreciacionTotal > maxDepreciacion) {
                    depreciacionTotal = maxDepreciacion;
                }

                campo.setDepreciacionAcumulada(depreciacionTotal);
                campo.setFechaUltimaDepreciacion(LocalDate.now());

                // Update años cepa if vida útil is defined
                if (campo.getVidaUtilAnios() != null && campo.getFechaInicioDepreciacion() != null) {
                    long mesesTranscurridos = java.time.temporal.ChronoUnit.MONTHS.between(
                            campo.getFechaInicioDepreciacion(), LocalDate.now());
                    campo.setAnosCepa((int) (mesesTranscurridos / 12));
                }

                repositoryCommand.save(campo);
                actualizados++;
            }
        }

        return actualizados;
    }

    @Override
    public List<CampoDto> findByBloqueId(UUID bloqueId) {
        return repositoryQuery.findByBloqueId(bloqueId).stream()
                .map(Campo::toAggregate)
                .collect(Collectors.toList());
    }
}
