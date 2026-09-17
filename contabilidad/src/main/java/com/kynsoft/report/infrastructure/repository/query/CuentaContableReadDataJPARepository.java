package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.domain.dto.TipoCuenta;
import com.kynsoft.report.infrastructure.entity.CuentaContable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface CuentaContableReadDataJPARepository
        extends JpaRepository<CuentaContable, UUID>, JpaSpecificationExecutor<CuentaContable> {

    @EntityGraph(attributePaths = {"cuentaPadre"})
    Optional<CuentaContable> findByCodigo(String codigo);

    @EntityGraph(attributePaths = {"cuentaPadre"})
    Optional<CuentaContable> findByCodigoAndActivoTrue(String codigo);

    @EntityGraph(attributePaths = {"cuentaPadre"})
    Optional<CuentaContable> findById(UUID id);

    @EntityGraph(attributePaths = {"cuentaPadre"})
    List<CuentaContable> findByActivoTrue();

    @EntityGraph(attributePaths = {"cuentaPadre"})
    List<CuentaContable> findByTipoAndActivoTrue(TipoCuenta tipo);

    @EntityGraph(attributePaths = {"cuentaPadre"})
    List<CuentaContable> findByCuentaPadreIdAndActivoTrue(UUID cuentaPadreId);

    List<CuentaContable> findByEsCentroCostoTrueAndActivoTrue();

    List<CuentaContable> findByPermiteMovimientoTrueAndActivoTrue();

    @EntityGraph(attributePaths = {"cuentaPadre"})
    @Query("SELECT cc FROM CuentaContable cc WHERE cc.nivel = 1 AND cc.activo = true ORDER BY cc.codigo")
    List<CuentaContable> findGruposPrincipales();

    @EntityGraph(attributePaths = {"cuentaPadre"})
    @Query("SELECT cc FROM CuentaContable cc WHERE cc.codigo LIKE :prefijo% AND cc.activo = true ORDER BY cc.codigo")
    List<CuentaContable> findByCodigoStartingWith(@Param("prefijo") String prefijo);

    @EntityGraph(attributePaths = {"cuentaPadre"})
    @Query("SELECT cc FROM CuentaContable cc WHERE cc.permiteMovimiento = true AND cc.activo = true ORDER BY cc.codigo")
    List<CuentaContable> findCuentasMovibles();

    @EntityGraph(attributePaths = {"cuentaPadre"})
    @Query("SELECT cc FROM CuentaContable cc WHERE cc.esCentroCosto = true AND cc.activo = true ORDER BY cc.codigo")
    List<CuentaContable> findCentrosCosto();

    @EntityGraph(attributePaths = {"cuentaPadre"})
    @Query("SELECT cc FROM CuentaContable cc " +
           "WHERE (LOWER(cc.codigo) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(cc.nombre) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND cc.activo = true ORDER BY cc.codigo")
    List<CuentaContable> search(@Param("query") String query);

    @EntityGraph(attributePaths = {"cuentaPadre"})
    Page<CuentaContable> findByActivoTrue(Pageable pageable);

    boolean existsByCodigo(String codigo);

    @Query("SELECT COUNT(cc) FROM CuentaContable cc WHERE cc.activo = true")
    Long countActivas();
}
