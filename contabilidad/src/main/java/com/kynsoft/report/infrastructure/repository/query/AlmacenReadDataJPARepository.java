package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Almacen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface AlmacenReadDataJPARepository extends JpaRepository<Almacen, UUID>, JpaSpecificationExecutor<Almacen> {

    @Override
    @EntityGraph(attributePaths = {"finca"})
    Optional<Almacen> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = {"finca"})
    Page<Almacen> findAll(Specification specification, Pageable pageable);

    @EntityGraph(attributePaths = {"finca"})
    Optional<Almacen> findByInventario(String inventario);

    @EntityGraph(attributePaths = {"finca"})
    Optional<Almacen> findByNombre(String nombre);

    @Query("SELECT COUNT(afp) FROM AlmacenFincaProducto afp WHERE afp.almacen.id = :almacenId AND afp.activo = true")
    int countProductosByAlmacenId(@Param("almacenId") UUID almacenId);

    @EntityGraph(attributePaths = {"finca", "almacenProductos", "almacenProductos.fincaProducto", "almacenProductos.fincaProducto.producto"})
    @Query("SELECT a FROM Almacen a WHERE a.id = :id")
    Optional<Almacen> findByIdWithProductos(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"finca"})
    Page<Almacen> findByFincaIdAndActivoTrue(UUID fincaId, Pageable pageable);

    @EntityGraph(attributePaths = {"finca"})
    Optional<Almacen> findByFincaIdAndEsPrincipalTrue(UUID fincaId);

    @Query("SELECT COUNT(a) FROM Almacen a WHERE a.finca.id = :fincaId")
    Long countByFincaId(@Param("fincaId") UUID fincaId);

    boolean existsByNombreIgnoreCaseAndFincaIdAndActivoTrue(String nombre, UUID fincaId);

    boolean existsByNombreIgnoreCaseAndFincaIdAndIdNotAndActivoTrue(String nombre, UUID fincaId, UUID id);
}
