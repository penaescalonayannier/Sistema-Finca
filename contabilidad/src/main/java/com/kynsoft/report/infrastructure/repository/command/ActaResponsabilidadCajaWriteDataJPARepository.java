package com.kynsoft.report.infrastructure.repository.command;
import com.kynsoft.report.infrastructure.entity.ActaResponsabilidadCaja;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface ActaResponsabilidadCajaWriteDataJPARepository extends JpaRepository<ActaResponsabilidadCaja, UUID> { @Query(value="select nextval('acta_responsabilidad_caja_numero_seq')", nativeQuery=true) Long siguienteNumero(); @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select a from ActaResponsabilidadCaja a where a.id=:id") Optional<ActaResponsabilidadCaja> findByIdForUpdate(@Param("id") UUID id); }
