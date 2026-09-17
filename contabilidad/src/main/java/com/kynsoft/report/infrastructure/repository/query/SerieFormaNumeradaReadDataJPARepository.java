package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.domain.dto.EstadoSerieFormaNumerada;
import com.kynsoft.report.infrastructure.entity.SerieFormaNumerada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface SerieFormaNumeradaReadDataJPARepository extends JpaRepository<SerieFormaNumerada, UUID> {
    List<SerieFormaNumerada> findByFormaIdOrderByAnioDescFechaInicioDesc(UUID formaId);
    List<SerieFormaNumerada> findByEstadoOrderByFechaInicioDesc(EstadoSerieFormaNumerada estado);

    @Query("SELECT s FROM SerieFormaNumerada s WHERE "
            + "(:fincaId IS NULL OR (s.alcanceTipo = com.kynsoft.report.domain.dto.AlcanceFormaNumerada.FINCA "
            + "AND s.alcanceId = :fincaId) "
            + "OR s.alcanceTipo = com.kynsoft.report.domain.dto.AlcanceFormaNumerada.ENTIDAD) "
            + "AND (:anio IS NULL OR s.anio = :anio OR s.anio IS NULL) "
            + "ORDER BY s.fechaInicio DESC")
    List<SerieFormaNumerada> buscarDisponibles(@Param("fincaId") UUID fincaId, @Param("anio") Integer anio);
}
