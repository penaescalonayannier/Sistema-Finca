package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.Labor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LaborWriteDataJPARepository extends JpaRepository<Labor, UUID> {
}
