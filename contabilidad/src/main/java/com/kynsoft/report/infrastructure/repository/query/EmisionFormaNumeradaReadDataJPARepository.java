package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.EmisionFormaNumerada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface EmisionFormaNumeradaReadDataJPARepository extends JpaRepository<EmisionFormaNumerada, UUID> {
    List<EmisionFormaNumerada> findBySerieIdOrderByNumeroDesc(UUID serieId);
    List<EmisionFormaNumerada> findByFormaIdAndFechaEmisionBetweenOrderByFechaEmisionDesc(
            UUID formaId, LocalDateTime desde, LocalDateTime hasta);
}
