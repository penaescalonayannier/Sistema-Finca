package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.domain.dto.EstadoTransferenciaAlmacen;
import com.kynsoft.report.infrastructure.entity.TransferenciaAlmacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface TransferenciaAlmacenReadDataJPARepository extends JpaRepository<TransferenciaAlmacen, UUID> {
    List<TransferenciaAlmacen> findByDestinoAlmacenIdAndEstadoOrderByFechaDespachoAsc(UUID destinoAlmacenId, EstadoTransferenciaAlmacen estado);
    List<TransferenciaAlmacen> findByFincaIdOrderByFechaDespachoDesc(UUID fincaId);
    @Query("select t.numeroDocumento from TransferenciaAlmacen t where t.fincaId = :fincaId and t.fechaDespacho >= :inicio and t.fechaDespacho < :fin")
    List<String> findNumerosDocumentoPorFincaYAnio(UUID fincaId, LocalDateTime inicio, LocalDateTime fin);
}
