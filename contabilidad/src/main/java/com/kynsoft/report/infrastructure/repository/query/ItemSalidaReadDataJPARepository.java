package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.ItemSalida;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface ItemSalidaReadDataJPARepository extends JpaRepository<ItemSalida, UUID> {

    @EntityGraph(attributePaths = {"trabajador"})
    List<ItemSalida> findBySalidaId(UUID salidaId);

    List<ItemSalida> findByTrabajadorId(UUID trabajadorId);

    List<ItemSalida> findBySalidaIdIn(List<UUID> salidaIds);
}
