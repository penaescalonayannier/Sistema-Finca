package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TrabajadorWriteDataJPARepository extends JpaRepository<Trabajador, UUID> {

    @Modifying
    @Query("UPDATE Trabajador t SET t.grupoId = null WHERE t.grupoId = :grupoId")
    int desasignarTrabajadoresDeGrupo(@Param("grupoId") UUID grupoId);
}
