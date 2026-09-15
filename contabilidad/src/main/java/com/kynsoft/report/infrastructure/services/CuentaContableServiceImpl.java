package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.CuentaContableDto;
import com.kynsoft.report.domain.dto.TipoCuenta;
import com.kynsoft.report.domain.services.ICuentaContableService;
import com.kynsoft.report.infrastructure.entity.CuentaContable;
import com.kynsoft.report.infrastructure.repository.command.CuentaContableWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.CuentaContableReadDataJPARepository;
import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.response.ErrorField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Cuentas Contables.
 * Permite CRUD completo para centros de costo y cuentas.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class CuentaContableServiceImpl implements ICuentaContableService {

    private final CuentaContableReadDataJPARepository readRepository;
    private final CuentaContableWriteDataJPARepository writeRepository;

    public CuentaContableServiceImpl(
            CuentaContableReadDataJPARepository readRepository,
            CuentaContableWriteDataJPARepository writeRepository) {
        this.readRepository = readRepository;
        this.writeRepository = writeRepository;
    }

    // ==================== CRUD ====================

    @Override
    @Transactional
    public UUID create(CuentaContableDto dto) {
        // Validar código único
        if (readRepository.existsByCodigo(dto.getCodigo())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("codigo", "Ya existe una cuenta con el código: " + dto.getCodigo())));
        }

        // Validar cuenta padre si se especifica
        if (dto.getCuentaPadreId() != null) {
            readRepository.findById(dto.getCuentaPadreId())
                    .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("cuentaPadreId", "Cuenta padre no encontrada"))));
        }

        CuentaContable entity = new CuentaContable(dto);
        writeRepository.save(entity);
        log.info("Cuenta contable creada: {} - {}", entity.getCodigo(), entity.getNombre());
        return entity.getId();
    }

    @Override
    @Transactional
    public void update(CuentaContableDto dto) {
        CuentaContable entity = readRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Cuenta contable no encontrada"))));

        // Validar código único si cambió
        if (!entity.getCodigo().equals(dto.getCodigo()) && readRepository.existsByCodigo(dto.getCodigo())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("codigo", "Ya existe una cuenta con el código: " + dto.getCodigo())));
        }

        // Validar cuenta padre si se especifica
        if (dto.getCuentaPadreId() != null) {
            readRepository.findById(dto.getCuentaPadreId())
                    .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("cuentaPadreId", "Cuenta padre no encontrada"))));
        }

        entity.setCodigo(dto.getCodigo());
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setTipo(dto.getTipo());
        entity.setNaturaleza(dto.getNaturaleza());
        entity.setNivel(dto.getNivel());
        entity.setCuentaPadreId(dto.getCuentaPadreId());
        entity.setPermiteMovimiento(dto.getPermiteMovimiento());
        entity.setEsCentroCosto(dto.getEsCentroCosto());
        entity.setActivo(dto.getActivo());

        writeRepository.save(entity);
        log.info("Cuenta contable actualizada: {} - {}", entity.getCodigo(), entity.getNombre());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        CuentaContable entity = readRepository.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Cuenta contable no encontrada"))));

        // Verificar si tiene subcuentas
        List<CuentaContable> subcuentas = readRepository.findByCodigoStartingWith(entity.getCodigo());
        if (subcuentas.size() > 1) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "No se puede eliminar: la cuenta tiene subcuentas")));
        }

        writeRepository.delete(entity);
        log.info("Cuenta contable eliminada: {} - {}", entity.getCodigo(), entity.getNombre());
    }

    @Override
    @Transactional
    public void activar(UUID id) {
        readRepository.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Cuenta contable no encontrada"))));

        writeRepository.activar(id);
        log.info("Cuenta contable activada: {}", id);
    }

    @Override
    @Transactional
    public void desactivar(UUID id) {
        readRepository.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Cuenta contable no encontrada"))));

        writeRepository.desactivar(id);
        log.info("Cuenta contable desactivada: {}", id);
    }

    // ==================== CONSULTAS ====================

    @Override
    public CuentaContableDto findById(UUID id) {
        return readRepository.findById(id)
                .map(CuentaContable::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Cuenta contable no encontrada: " + id))));
    }

    @Override
    public CuentaContableDto findByCodigo(String codigo) {
        return readRepository.findByCodigoAndActivoTrue(codigo)
                .map(CuentaContable::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("codigo", "Cuenta contable no encontrada: " + codigo))));
    }

    @Override
    public Optional<CuentaContableDto> findByCodigoOptional(String codigo) {
        return readRepository.findByCodigoAndActivoTrue(codigo)
                .map(CuentaContable::toAggregate);
    }

    @Override
    public List<CuentaContableDto> findAll() {
        return readRepository.findByActivoTrue().stream()
                .map(CuentaContable::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<CuentaContableDto> findByTipo(TipoCuenta tipo) {
        return readRepository.findByTipoAndActivoTrue(tipo).stream()
                .map(CuentaContable::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<CuentaContableDto> findSubcuentas(UUID cuentaPadreId) {
        return readRepository.findByCuentaPadreIdAndActivoTrue(cuentaPadreId).stream()
                .map(CuentaContable::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<CuentaContableDto> findGruposPrincipales() {
        return readRepository.findGruposPrincipales().stream()
                .map(CuentaContable::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<CuentaContableDto> findCuentasMovibles() {
        return readRepository.findCuentasMovibles().stream()
                .map(CuentaContable::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<CuentaContableDto> findCentrosCosto() {
        return readRepository.findCentrosCosto().stream()
                .map(CuentaContable::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<CuentaContableDto> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }
        return readRepository.search(query.trim()).stream()
                .map(CuentaContable::toAggregate)
                .collect(Collectors.toList());
    }

    // ==================== VALIDACIONES ====================

    @Override
    public boolean existsByCodigo(String codigo) {
        return readRepository.existsByCodigo(codigo);
    }

    @Override
    public boolean esCuentaMovible(String codigo) {
        return readRepository.findByCodigoAndActivoTrue(codigo)
                .map(CuentaContable::getPermiteMovimiento)
                .orElse(false);
    }

    @Override
    public boolean esCentroCosto(String codigo) {
        return readRepository.findByCodigoAndActivoTrue(codigo)
                .map(CuentaContable::getEsCentroCosto)
                .orElse(false);
    }

    // ==================== UTILIDADES ====================

    @Override
    public Optional<CuentaContableDto> getCuentaPadre(String codigo) {
        return readRepository.findByCodigoAndActivoTrue(codigo)
                .filter(cc -> cc.getCuentaPadreId() != null)
                .flatMap(cc -> readRepository.findById(cc.getCuentaPadreId()))
                .map(CuentaContable::toAggregate);
    }

    @Override
    public List<CuentaContableDto> getJerarquiaCompleta(String codigoPadre) {
        List<CuentaContableDto> resultado = new ArrayList<>();

        // Obtener cuenta padre
        Optional<CuentaContable> padreOpt = readRepository.findByCodigoAndActivoTrue(codigoPadre);
        if (padreOpt.isEmpty()) {
            return resultado;
        }

        resultado.add(padreOpt.get().toAggregate());

        // Obtener todas las subcuentas que empiezan con el código padre
        List<CuentaContable> subcuentas = readRepository.findByCodigoStartingWith(codigoPadre);
        for (CuentaContable subcuenta : subcuentas) {
            if (!subcuenta.getCodigo().equals(codigoPadre)) {
                resultado.add(subcuenta.toAggregate());
            }
        }

        return resultado;
    }

    @Override
    public void validarCuentaParaMovimiento(String codigo) {
        CuentaContable cuenta = readRepository.findByCodigoAndActivoTrue(codigo)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("codigo", "Cuenta contable no encontrada: " + codigo))));

        if (!Boolean.TRUE.equals(cuenta.getPermiteMovimiento())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("codigo", "La cuenta " + codigo + " no permite movimientos. Es una cuenta de agrupación.")));
        }
    }

    @Override
    public Long countActivas() {
        return readRepository.countActivas();
    }
}
