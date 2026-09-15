package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.ActivoFijoTangible;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivoFijoTangibleReadRepository extends JpaRepository<ActivoFijoTangible, UUID> {

    Optional<ActivoFijoTangible> findByNumeroInventario(String numeroInventario);

    List<ActivoFijoTangible> findByGrupoId(UUID grupoId);

    List<ActivoFijoTangible> findByFincaId(UUID fincaId);

    List<ActivoFijoTangible> findByActivoTrueAndFechaBajaIsNull();

    @Query("SELECT a FROM ActivoFijoTangible a " +
           "WHERE (:query IS NULL OR LOWER(a.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "       OR LOWER(a.numeroInventario) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:grupoId IS NULL OR a.grupo.id = :grupoId) " +
           "AND (:fincaId IS NULL OR a.finca.id = :fincaId) " +
           "AND (:activo IS NULL OR a.activo = :activo)")
    Page<ActivoFijoTangible> search(@Param("query") String query,
                                     @Param("grupoId") UUID grupoId,
                                     @Param("fincaId") UUID fincaId,
                                     @Param("activo") Boolean activo,
                                     Pageable pageable);
}
