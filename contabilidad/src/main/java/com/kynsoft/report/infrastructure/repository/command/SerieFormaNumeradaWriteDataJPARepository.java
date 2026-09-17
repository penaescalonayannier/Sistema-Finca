package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.domain.dto.AlcanceFormaNumerada;
import com.kynsoft.report.domain.dto.EstadoSerieFormaNumerada;
import com.kynsoft.report.infrastructure.entity.SerieFormaNumerada;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SerieFormaNumeradaWriteDataJPARepository extends JpaRepository<SerieFormaNumerada, UUID> {

    /** La reserva del próximo número debe tomar este bloqueo dentro de la transacción de emisión. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SerieFormaNumerada s "
            + "WHERE s.formaId = :formaId AND s.alcanceTipo = :alcanceTipo "
            + "AND ((:alcanceId IS NULL AND s.alcanceId IS NULL) OR s.alcanceId = :alcanceId) "
            + "AND ((:anio IS NULL AND s.anio IS NULL) OR s.anio = :anio) "
            + "AND s.estado = :estado")
    Optional<SerieFormaNumerada> findActivaForUpdate(
            @Param("formaId") UUID formaId,
            @Param("alcanceTipo") AlcanceFormaNumerada alcanceTipo,
            @Param("alcanceId") UUID alcanceId,
            @Param("anio") Integer anio,
            @Param("estado") EstadoSerieFormaNumerada estado);
}
