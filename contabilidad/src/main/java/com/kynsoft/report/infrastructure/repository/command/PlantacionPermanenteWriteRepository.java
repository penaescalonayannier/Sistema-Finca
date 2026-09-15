package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.PlantacionPermanente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlantacionPermanenteWriteRepository extends JpaRepository<PlantacionPermanente, UUID> {
}
