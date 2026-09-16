package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.MovimientoCajaDenominacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface MovimientoCajaDenominacionReadDataJPARepository extends JpaRepository<MovimientoCajaDenominacion, UUID> {
    List<MovimientoCajaDenominacion> findByMovimientoCajaIdIn(Collection<UUID> movimientoCajaIds);
}
