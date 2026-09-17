package com.kynsoft.report.infrastructure.repository.command;
import com.kynsoft.report.infrastructure.entity.AreaTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface AreaTrabajoWriteDataJPARepository extends JpaRepository<AreaTrabajo, UUID> {}
