package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.ItemSalida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ItemSalidaWriteDataJPARepository extends JpaRepository<ItemSalida, UUID> {
    void deleteBySalidaId(UUID salidaId);
}
