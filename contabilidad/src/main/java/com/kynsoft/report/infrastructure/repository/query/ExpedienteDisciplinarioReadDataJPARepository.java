package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.ExpedienteDisciplinario;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface ExpedienteDisciplinarioReadDataJPARepository extends JpaRepository<ExpedienteDisciplinario, UUID> {
    @Query("select e from ExpedienteDisciplinario e where e.fincaId = :fincaId "
            + "and (:trabajadorId is null or e.trabajadorId = :trabajadorId) "
            + "and (:desde is null or e.fecha >= :desde) and (:hasta is null or e.fecha <= :hasta) "
            + "and (:incluirAnulados = true or e.estado <> com.kynsoft.report.domain.dto.EstadoExpedienteDisciplinario.ANULADA) "
            + "order by e.fecha desc, e.creadoEn desc")
    List<ExpedienteDisciplinario> buscar(@Param("fincaId") UUID fincaId, @Param("trabajadorId") UUID trabajadorId,
                                         @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta,
                                         @Param("incluirAnulados") boolean incluirAnulados);
}
