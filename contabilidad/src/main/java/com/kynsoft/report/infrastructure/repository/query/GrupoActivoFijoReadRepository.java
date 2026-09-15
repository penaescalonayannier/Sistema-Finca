package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.GrupoActivoFijo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GrupoActivoFijoReadRepository extends JpaRepository<GrupoActivoFijo, UUID> {

    Optional<GrupoActivoFijo> findByCodigo(String codigo);

    List<GrupoActivoFijo> findByActivoTrue();

    List<GrupoActivoFijo> findAllByOrderByCodigoAsc();
}
