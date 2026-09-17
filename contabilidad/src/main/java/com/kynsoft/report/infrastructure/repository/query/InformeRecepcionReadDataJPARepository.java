package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.InformeRecepcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.*;

public interface InformeRecepcionReadDataJPARepository extends JpaRepository<InformeRecepcion, UUID> {
    @Query("select distinct i from InformeRecepcion i left join fetch i.lineas where i.id = :id")
    Optional<InformeRecepcion> findDetalleById(@Param("id") UUID id);
    @Query("select i.numeroDocumento from InformeRecepcion i where i.fincaId = :fincaId and i.createdAt >= :inicio and i.createdAt < :fin")
    List<String> findNumerosDocumentoPorFincaYAnio(@Param("fincaId") UUID fincaId,
        @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
