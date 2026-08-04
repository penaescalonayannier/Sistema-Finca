package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.UnidadMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UnidadMedidaWriteDataJPARepository extends JpaRepository<UnidadMedida, UUID>{
}
