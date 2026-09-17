package com.kynsoft.report.infrastructure.repository.command;
import com.kynsoft.report.infrastructure.entity.ConteoFisicoLinea; import org.springframework.data.jpa.repository.JpaRepository; import java.util.UUID;
public interface ConteoFisicoLineaWriteDataJPARepository extends JpaRepository<ConteoFisicoLinea, UUID> {}
