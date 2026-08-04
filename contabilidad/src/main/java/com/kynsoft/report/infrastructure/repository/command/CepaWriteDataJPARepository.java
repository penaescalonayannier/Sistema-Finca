package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.Cepa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CepaWriteDataJPARepository extends JpaRepository<Cepa, UUID> {
}
