package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.AlmacenFincaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AlmacenFincaProductoWriteDataJPARepository extends JpaRepository<AlmacenFincaProducto, UUID> {

    @Modifying
    @Query("UPDATE AlmacenFincaProducto afp SET afp.stock = afp.stock + :cantidad WHERE afp.id = :id")
    void incrementarStock(@Param("id") UUID id, @Param("cantidad") Double cantidad);

    @Modifying
    @Query("UPDATE AlmacenFincaProducto afp SET afp.stock = afp.stock - :cantidad WHERE afp.id = :id AND afp.stock >= :cantidad")
    int decrementarStock(@Param("id") UUID id, @Param("cantidad") Double cantidad);
}
