package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.CuentaContableDto;
import com.kynsoft.report.domain.dto.TipoCuenta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestión de Cuentas Contables según Nomenclador Cubano.
 * Permite CRUD completo para centros de costo y cuentas.
 */
public interface ICuentaContableService {

    // ==================== CRUD ====================

    UUID create(CuentaContableDto dto);

    void update(CuentaContableDto dto);

    void delete(UUID id);

    void activar(UUID id);

    void desactivar(UUID id);

    // ==================== CONSULTAS ====================

    CuentaContableDto findById(UUID id);

    CuentaContableDto findByCodigo(String codigo);

    Optional<CuentaContableDto> findByCodigoOptional(String codigo);

    List<CuentaContableDto> findAll();

    List<CuentaContableDto> findByTipo(TipoCuenta tipo);

    List<CuentaContableDto> findSubcuentas(UUID cuentaPadreId);

    List<CuentaContableDto> findGruposPrincipales();

    List<CuentaContableDto> findCuentasMovibles();

    List<CuentaContableDto> findCentrosCosto();

    List<CuentaContableDto> search(String query);

    // ==================== VALIDACIONES ====================

    boolean existsByCodigo(String codigo);

    boolean esCuentaMovible(String codigo);

    boolean esCentroCosto(String codigo);

    // ==================== UTILIDADES ====================

    /**
     * Obtiene la cuenta padre de una cuenta dada
     */
    Optional<CuentaContableDto> getCuentaPadre(String codigo);

    /**
     * Obtiene todas las subcuentas (recursivo) de una cuenta
     */
    List<CuentaContableDto> getJerarquiaCompleta(String codigoPadre);

    /**
     * Valida que un código de cuenta exista y permita movimientos
     */
    void validarCuentaParaMovimiento(String codigo);

    Long countActivas();
}
