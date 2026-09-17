package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.HistorialSalario;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialSalarioReadDataJPARepository extends JpaRepository<HistorialSalario, UUID> {
    List<HistorialSalario> findByTrabajadorIdOrderByFechaVigenciaDesc(UUID trabajadorId);
    boolean existsByTrabajadorIdAndFechaVigencia(UUID trabajadorId, LocalDate fechaVigencia);
    Optional<HistorialSalario> findFirstByTrabajadorIdAndEstadoAndFechaVigenciaLessThanEqualOrderByFechaVigenciaDesc(
            UUID trabajadorId, String estado, LocalDate fechaVigencia);
}
