package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.MovimientoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.Collection;
import java.util.List;

@Repository
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface MovimientoCajaReadDataJPARepository extends JpaRepository<MovimientoCaja, UUID> {
    @Query("SELECT COALESCE(SUM(m.importe), 0) FROM MovimientoCaja m WHERE m.fincaId = :fincaId")
    Double saldoByFincaId(@Param("fincaId") UUID fincaId);

    @Query("SELECT COALESCE(SUM(m.importe), 0) FROM MovimientoCaja m " +
           "WHERE m.fincaId = :fincaId AND (m.tipo = 'COBRO_EFECTIVO' OR m.tipo = 'VUELTO_EFECTIVO')")
    Double totalCobradoEfectivoByFincaId(@Param("fincaId") UUID fincaId);

    @Query("SELECT COALESCE(SUM(-m.importe), 0) FROM MovimientoCaja m " +
           "WHERE m.fincaId = :fincaId AND m.tipo = 'ENTREGA_BANCO'")
    Double totalEntregadoBancoByFincaId(@Param("fincaId") UUID fincaId);

    List<MovimientoCaja> findByEntregaBancoIdIn(Collection<UUID> entregaBancoIds);
}
