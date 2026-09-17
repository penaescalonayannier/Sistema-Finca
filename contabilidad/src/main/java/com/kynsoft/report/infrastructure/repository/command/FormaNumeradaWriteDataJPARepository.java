package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.FormaNumerada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FormaNumeradaWriteDataJPARepository extends JpaRepository<FormaNumerada, UUID> {
    Optional<FormaNumerada> findByCodigo(String codigo);
}
