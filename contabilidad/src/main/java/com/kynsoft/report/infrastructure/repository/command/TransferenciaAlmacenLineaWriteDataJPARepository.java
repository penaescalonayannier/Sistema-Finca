package com.kynsoft.report.infrastructure.repository.command;
import com.kynsoft.report.infrastructure.entity.TransferenciaAlmacenLinea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface TransferenciaAlmacenLineaWriteDataJPARepository extends JpaRepository<TransferenciaAlmacenLinea, UUID> { }
