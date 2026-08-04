package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CargoWriteDataJPARepository extends JpaRepository<Cargo, UUID> {}
