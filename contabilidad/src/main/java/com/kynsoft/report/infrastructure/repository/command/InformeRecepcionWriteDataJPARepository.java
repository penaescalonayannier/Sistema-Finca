package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.InformeRecepcion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface InformeRecepcionWriteDataJPARepository extends JpaRepository<InformeRecepcion, UUID> { }
