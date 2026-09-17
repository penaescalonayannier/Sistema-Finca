package com.kynsoft.report.infrastructure.repository.command;
import com.kynsoft.report.infrastructure.entity.Plaza;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface PlazaWriteDataJPARepository extends JpaRepository<Plaza, UUID> {}
