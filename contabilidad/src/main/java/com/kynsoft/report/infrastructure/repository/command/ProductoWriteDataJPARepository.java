package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProductoWriteDataJPARepository extends JpaRepository<Producto, UUID> {
}