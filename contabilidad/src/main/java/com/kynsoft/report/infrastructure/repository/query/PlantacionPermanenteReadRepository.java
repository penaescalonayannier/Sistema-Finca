package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.domain.dto.enums.TipoPlantacion;
import com.kynsoft.report.infrastructure.entity.PlantacionPermanente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlantacionPermanenteReadRepository extends JpaRepository<PlantacionPermanente, UUID> {

    List<PlantacionPermanente> findByBloque(Integer bloque);

    List<PlantacionPermanente> findByTipoPlantacion(TipoPlantacion tipoPlantacion);

    List<PlantacionPermanente> findByFincaId(UUID fincaId);

    @Query("SELECT p FROM PlantacionPermanente p " +
           "WHERE (:query IS NULL OR LOWER(p.numeroInventario) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "       OR LOWER(p.codigoVariedad) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:tipoPlantacion IS NULL OR p.tipoPlantacion = :tipoPlantacion) " +
           "AND (:bloque IS NULL OR p.bloque = :bloque) " +
           "AND (:fincaId IS NULL OR p.fincaId = :fincaId) " +
           "AND p.activo = true")
    Page<PlantacionPermanente> search(@Param("query") String query,
                                       @Param("tipoPlantacion") TipoPlantacion tipoPlantacion,
                                       @Param("bloque") Integer bloque,
                                       @Param("fincaId") UUID fincaId,
                                       Pageable pageable);

    @Query("SELECT p.tipoPlantacion, SUM(p.areaHectareas), SUM(p.valorAdquisicion), COUNT(p) " +
           "FROM PlantacionPermanente p " +
           "WHERE (:fincaId IS NULL OR p.fincaId = :fincaId) " +
           "AND p.activo = true " +
           "GROUP BY p.tipoPlantacion")
    List<Object[]> getResumenPorTipo(@Param("fincaId") UUID fincaId);
}
