package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.FincaProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FincaProductoWriteDataJPARepository extends JpaRepository<FincaProducto, UUID> {
    void deleteByFincaId(UUID fincaId);
    void deleteByProductoId(UUID productoId);
}