package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.TrabajadorResponse;
import com.kynsoft.report.domain.dto.DeleteTrabajadorResponse;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.dto.TransferirTrabajadorResponse;
import com.kynsoft.report.domain.services.IFincaService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import com.kynsoft.report.infrastructure.entity.DeudaTrabajador;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.entity.TrabajadorDia;
import com.kynsoft.report.infrastructure.repository.command.TrabajadorWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.DeudaTrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorDiaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantContext;
import com.kynsoft.report.infrastructure.security.TenantSpecification;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrabajadorServiceImpl implements ITrabajadorService {

    private final TrabajadorWriteDataJPARepository repositoryCommand;
    private final TrabajadorReadDataJPARepository repositoryQuery;
    private final FincaReadDataJPARepository fincaRepository;
    private final DeudaTrabajadorReadDataJPARepository deudaRepository;
    private final TrabajadorDiaReadDataJPARepository trabajadorDiaRepository;

    public TrabajadorServiceImpl(TrabajadorWriteDataJPARepository repositoryCommand,
                                  TrabajadorReadDataJPARepository repositoryQuery,
                                  FincaReadDataJPARepository fincaRepository,
                                  DeudaTrabajadorReadDataJPARepository deudaRepository,
                                  TrabajadorDiaReadDataJPARepository trabajadorDiaRepository) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.fincaRepository = fincaRepository;
        this.deudaRepository = deudaRepository;
        this.trabajadorDiaRepository = trabajadorDiaRepository;
    }

    @Override
    public void create(TrabajadorDto object) {
        // Validar que el RUC no exista
        repositoryQuery.findByRuc(object.getRuc())
            .ifPresent(t -> {
                throw new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("ruc", "Ya existe un trabajador con RUC " + object.getRuc())));
            });

        repositoryCommand.save(new Trabajador(object));
    }

    @Override
    public void update(TrabajadorDto object) {
        // Buscar la entidad existente desde el repositorio de ESCRITURA
        // para mantener el mismo contexto de persistencia
        Trabajador trabajador = repositoryCommand.findById(object.getId())
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Trabajador no encontrado."))));

        // Actualizar solo los campos modificables
        // RUC no se modifica (regla de negocio RN-09)
        trabajador.setNombre(object.getNombre());
        trabajador.setCuenta(object.getCuenta());
        trabajador.setFincaId(object.getFincaId());
        trabajador.setGrupoId(object.getGrupoId());
        trabajador.setCargoId(object.getCargoId());
        if (object.getActivo() != null) {
            trabajador.setActivo(object.getActivo());
        }

        repositoryCommand.save(trabajador);
    }

    @Override
    public DeleteTrabajadorResponse delete(UUID id) {
        Trabajador trabajador = repositoryQuery.findById(id)
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Trabajador no encontrado."))));

        // Verificar que no esté ya inactivo
        if (!trabajador.getActivo()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("activo", "El trabajador ya está inactivo.")));
        }

        // Generar advertencias basadas en validaciones reales
        List<String> advertencias = generarAdvertenciasDesactivacion(id, trabajador.getNombre());

        // Soft delete: marcar como inactivo
        trabajador.setActivo(false);
        repositoryCommand.save(trabajador);

        return DeleteTrabajadorResponse.builder()
                .id(id)
                .advertencias(advertencias)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DeleteTrabajadorResponse previewDesactivacion(UUID id) {
        Trabajador trabajador = repositoryQuery.findById(id)
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Trabajador no encontrado."))));

        // Verificar que no esté ya inactivo
        if (!trabajador.getActivo()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("activo", "El trabajador ya está inactivo.")));
        }

        // Generar advertencias sin desactivar
        List<String> advertencias = generarAdvertenciasDesactivacion(id, trabajador.getNombre());

        return DeleteTrabajadorResponse.builder()
                .id(id)
                .advertencias(advertencias)
                .build();
    }

    private List<String> generarAdvertenciasDesactivacion(UUID trabajadorId, String nombreTrabajador) {
        List<String> advertencias = new ArrayList<>();

        // 1. Verificar deudas pendientes
        Optional<DeudaTrabajador> deudaOpt = deudaRepository.findByTrabajadorId(trabajadorId);
        if (deudaOpt.isPresent()) {
            DeudaTrabajador deuda = deudaOpt.get();
            if (deuda.getImporte() != null && deuda.getImporte() > 0) {
                advertencias.add(String.format(
                    "DEUDA PENDIENTE: El trabajador tiene una deuda de $%.2f que permanecerá en el sistema.",
                    deuda.getImporte()
                ));
            }
        }

        // 2. Verificar reportes del mes actual
        LocalDate hoy = LocalDate.now();
        String yearActual = String.valueOf(hoy.getYear());
        String mesActual = obtenerNombreMes(hoy.getMonthValue());

        List<TrabajadorDia> diasMesActual = trabajadorDiaRepository
            .findByTrabajadorIdAndYearAndMes(trabajadorId, yearActual, mesActual);

        if (!diasMesActual.isEmpty()) {
            int totalDias = diasMesActual.size();
            double totalHoras = diasMesActual.stream()
                .mapToDouble(td -> {
                    try {
                        return Double.parseDouble(td.getHoras());
                    } catch (Exception e) {
                        return 0.0;
                    }
                })
                .sum();
            advertencias.add(String.format(
                "REPORTES ACTIVOS: El trabajador tiene %d día(s) registrado(s) en %s %s con %.1f horas totales.",
                totalDias, mesActual, yearActual, totalHoras
            ));
        }

        // 3. Verificar reportes del mes anterior (para prenóminas pendientes)
        LocalDate mesAnterior = hoy.minusMonths(1);
        String yearAnterior = String.valueOf(mesAnterior.getYear());
        String mesAnteriorNombre = obtenerNombreMes(mesAnterior.getMonthValue());

        List<TrabajadorDia> diasMesAnterior = trabajadorDiaRepository
            .findByTrabajadorIdAndYearAndMes(trabajadorId, yearAnterior, mesAnteriorNombre);

        if (!diasMesAnterior.isEmpty()) {
            advertencias.add(String.format(
                "PRENÓMINA PENDIENTE: El trabajador tiene registros en %s %s que podrían estar pendientes de liquidación.",
                mesAnteriorNombre, yearAnterior
            ));
        }

        return advertencias;
    }

    private String obtenerNombreMes(int mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                          "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[mes - 1];
    }

    @Override
    public void reactivar(UUID id) {
        Trabajador trabajador = repositoryQuery.findById(id)
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Trabajador no encontrado."))));

        // Verificar que esté inactivo
        if (trabajador.getActivo()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("activo", "El trabajador ya está activo.")));
        }

        trabajador.setActivo(true);
        repositoryCommand.save(trabajador);
    }

    @Override
    public TransferirTrabajadorResponse transferir(UUID trabajadorId, UUID nuevaFincaId) {
        Trabajador trabajador = repositoryQuery.findById(trabajadorId)
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Trabajador no encontrado."))));

        // Validar que la nueva finca exista
        Finca nuevaFinca = fincaRepository.findById(nuevaFincaId)
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("nuevaFincaId", "Nueva finca no encontrada."))));

        // Validar que no sea la misma finca
        if (trabajador.getFincaId().equals(nuevaFincaId)) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("nuevaFincaId", "El trabajador ya pertenece a esa finca.")));
        }

        String fincaAnteriorNombre = trabajador.getFinca() != null ? trabajador.getFinca().getName() : "Desconocida";

        trabajador.setFincaId(nuevaFincaId);
        repositoryCommand.save(trabajador);

        return TransferirTrabajadorResponse.builder()
                .id(trabajadorId)
                .fincaAnterior(fincaAnteriorNombre)
                .fincaNueva(nuevaFinca.getName())
                .build();
    }

    @Override
    public TrabajadorDto findById(UUID id) {
        return repositoryQuery.findByIdWithRelations(id)
                .map(Trabajador::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Trabajador no encontrado."))));
    }

    @Override
    public TrabajadorDto findByRuc(String ruc) {
        return repositoryQuery.findByRuc(ruc)
                .map(Trabajador::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("ruc", "Trabajador con RUC " + ruc + " no encontrado."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Trabajador> specifications = new GenericSpecificationsBuilder<>(filterCriteria);

        // Add fetch joins for lazy-loaded relationships (only for non-count queries)
        Specification<Trabajador> fetchSpec = (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("finca", jakarta.persistence.criteria.JoinType.LEFT);
                root.fetch("grupo", jakarta.persistence.criteria.JoinType.LEFT);
                root.fetch("cargo", jakarta.persistence.criteria.JoinType.LEFT);
            }
            return cb.conjunction();
        };

        Specification<Trabajador> combinedSpec = Specification
                .where(specifications)
                .and(fetchSpec)
                .and(TenantSpecification.byFincaDirecta());

        Page<Trabajador> data = repositoryQuery.findAll(combinedSpec, pageable);
        return createPaginatedResponse(data);
    }

    @Override
    public PaginatedResponse findByFincaId(UUID fincaId, Pageable pageable) {
        Specification<Trabajador> spec = (root, query, cb) ->
                cb.and(
                    cb.equal(root.get("fincaId"), fincaId),
                    cb.equal(root.get("activo"), true)
                );

        // Add fetch joins for lazy-loaded relationships (only for non-count queries)
        Specification<Trabajador> fetchSpec = (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("finca", jakarta.persistence.criteria.JoinType.LEFT);
                root.fetch("grupo", jakarta.persistence.criteria.JoinType.LEFT);
                root.fetch("cargo", jakarta.persistence.criteria.JoinType.LEFT);
            }
            return cb.conjunction();
        };

        Specification<Trabajador> combinedSpec = Specification.where(spec).and(fetchSpec);
        Page<Trabajador> data = repositoryQuery.findAll(combinedSpec, pageable);
        return createPaginatedResponse(data);
    }

    @Override
    public PaginatedResponse findByGrupoId(UUID grupoId, Pageable pageable) {
        Specification<Trabajador> spec = (root, query, cb) ->
                cb.and(
                    cb.equal(root.get("grupoId"), grupoId),
                    cb.equal(root.get("activo"), true)
                );

        // Add fetch joins for lazy-loaded relationships (only for non-count queries)
        Specification<Trabajador> fetchSpec = (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("finca", jakarta.persistence.criteria.JoinType.LEFT);
                root.fetch("grupo", jakarta.persistence.criteria.JoinType.LEFT);
                root.fetch("cargo", jakarta.persistence.criteria.JoinType.LEFT);
            }
            return cb.conjunction();
        };

        Specification<Trabajador> combinedSpec = Specification
                .where(spec)
                .and(fetchSpec)
                .and(TenantSpecification.byFincaDirecta());
        Page<Trabajador> data = repositoryQuery.findAll(combinedSpec, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Trabajador> data) {
        List<TrabajadorResponse> responses = data.getContent().stream()
                .map(Trabajador::toAggregate)
                .map(TrabajadorResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public List<TrabajadorDto> findAll(List<UUID> ids) {
        return this.repositoryQuery.findAllById(ids).stream()
                .map(Trabajador::toAggregate)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrabajadorDto> findAllActivos() {
        UUID fincaId = TenantContext.getEffectiveFincaId();
        if (fincaId != null) {
            return this.repositoryQuery.findAllActivosWithRelationsByFincaId(fincaId).stream()
                    .map(Trabajador::toAggregate)
                    .toList();
        }
        // ADMIN sin finca seleccionada ve todos
        return this.repositoryQuery.findAllActivosWithRelations().stream()
                .map(Trabajador::toAggregate)
                .toList();
    }

    @Override
    public int desasignarTrabajadoresDeGrupo(UUID grupoId) {
        return repositoryCommand.desasignarTrabajadoresDeGrupo(grupoId);
    }
}
