package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.Variedad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface VariedadWriteDataJPARepository extends JpaRepository<Variedad, UUID>{
}
