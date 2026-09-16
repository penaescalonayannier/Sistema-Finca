package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.ItemSalida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemSalidaWriteDataJPARepository extends JpaRepository<ItemSalida, UUID> {
    void deleteBySalidaId(UUID salidaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM ItemSalida i WHERE i.id = :id")
    Optional<ItemSalida> findByIdForUpdate(@Param("id") UUID id);
}
