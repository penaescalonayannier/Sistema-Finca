package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.ConfiguracionEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfiguracionEmpresaReadDataJPARepository extends JpaRepository<ConfiguracionEmpresa, UUID> {

    /**
     * Encuentra la primera configuración de empresa activa ordenada por nombre.
     * Usa naming convention de Spring Data que automáticamente aplica LIMIT 1.
     */
    Optional<ConfiguracionEmpresa> findFirstByActivoTrueOrderByNombreAsc();
}
