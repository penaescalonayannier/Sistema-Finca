package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.CuentaContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CuentaContableWriteDataJPARepository extends JpaRepository<CuentaContable, UUID> {

    @Modifying
    @Query("UPDATE CuentaContable cc SET cc.activo = false WHERE cc.id = :id")
    void desactivar(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE CuentaContable cc SET cc.activo = true WHERE cc.id = :id")
    void activar(@Param("id") UUID id);
}
