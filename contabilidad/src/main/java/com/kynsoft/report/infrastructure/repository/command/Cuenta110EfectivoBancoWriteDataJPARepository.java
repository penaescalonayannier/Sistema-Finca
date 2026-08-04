package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.Cuenta110EfectivoBanco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface Cuenta110EfectivoBancoWriteDataJPARepository extends JpaRepository<Cuenta110EfectivoBanco, UUID> {}