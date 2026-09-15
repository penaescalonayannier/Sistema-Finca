package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.domain.dto.enums.CategoriaAnimal;
import com.kynsoft.report.domain.dto.enums.TipoGanado;
import com.kynsoft.report.infrastructure.entity.ActivoAnimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivoAnimalReadRepository extends JpaRepository<ActivoAnimal, UUID> {

    List<ActivoAnimal> findByTipoGanado(TipoGanado tipoGanado);

    List<ActivoAnimal> findByCategoria(CategoriaAnimal categoria);

    List<ActivoAnimal> findByFincaId(UUID fincaId);

    @Query("SELECT a FROM ActivoAnimal a " +
           "WHERE (:query IS NULL OR LOWER(a.numeroInventario) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "       OR LOWER(a.hierro) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "       OR LOWER(a.codigoArete) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:tipoGanado IS NULL OR a.tipoGanado = :tipoGanado) " +
           "AND (:categoria IS NULL OR a.categoria = :categoria) " +
           "AND (:fincaId IS NULL OR a.fincaId = :fincaId) " +
           "AND a.activo = true")
    Page<ActivoAnimal> search(@Param("query") String query,
                               @Param("tipoGanado") TipoGanado tipoGanado,
                               @Param("categoria") CategoriaAnimal categoria,
                               @Param("fincaId") UUID fincaId,
                               Pageable pageable);

    @Query("SELECT a.categoria, COUNT(a), SUM(a.valorAdquisicion) " +
           "FROM ActivoAnimal a " +
           "WHERE (:fincaId IS NULL OR a.fincaId = :fincaId) " +
           "AND a.activo = true " +
           "GROUP BY a.categoria")
    List<Object[]> getResumenPorCategoria(@Param("fincaId") UUID fincaId);
}
