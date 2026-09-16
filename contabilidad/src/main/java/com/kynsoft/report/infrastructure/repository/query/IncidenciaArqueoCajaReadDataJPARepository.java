package com.kynsoft.report.infrastructure.repository.query;
import com.kynsoft.report.domain.dto.EstadoIncidenciaArqueoCaja;
import com.kynsoft.report.infrastructure.entity.IncidenciaArqueoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Transactional(readOnly=true, transactionManager="readTransactionManager") public interface IncidenciaArqueoCajaReadDataJPARepository extends JpaRepository<IncidenciaArqueoCaja, UUID> { List<IncidenciaArqueoCaja> findByFincaIdOrderByFechaCreacionDesc(UUID fincaId); boolean existsByArqueoCajaId(UUID arqueoCajaId); long countByFincaIdAndEstadoIn(UUID fincaId, Collection<EstadoIncidenciaArqueoCaja> estados); }
