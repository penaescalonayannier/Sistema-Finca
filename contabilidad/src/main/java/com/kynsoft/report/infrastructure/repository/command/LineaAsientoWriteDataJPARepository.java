package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.LineaAsiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface LineaAsientoWriteDataJPARepository extends JpaRepository<LineaAsiento, UUID> {

    @Modifying
    @Query("DELETE FROM LineaAsiento l WHERE l.asiento.id = :asientoId")
    void deleteByAsientoId(@Param("asientoId") UUID asientoId);
}
