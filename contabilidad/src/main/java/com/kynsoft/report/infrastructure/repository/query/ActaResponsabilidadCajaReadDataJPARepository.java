package com.kynsoft.report.infrastructure.repository.query;
import com.kynsoft.report.domain.dto.EstadoActaResponsabilidadCaja;
import com.kynsoft.report.infrastructure.entity.ActaResponsabilidadCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Transactional(readOnly=true, transactionManager="readTransactionManager") public interface ActaResponsabilidadCajaReadDataJPARepository extends JpaRepository<ActaResponsabilidadCaja, UUID> { List<ActaResponsabilidadCaja> findByFincaIdOrderByFechaInicioDesc(UUID fincaId); boolean existsByFincaIdAndEstado(UUID fincaId, EstadoActaResponsabilidadCaja estado); }
