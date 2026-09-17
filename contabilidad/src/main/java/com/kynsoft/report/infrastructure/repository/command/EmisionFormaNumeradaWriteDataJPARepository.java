package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.EmisionFormaNumerada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmisionFormaNumeradaWriteDataJPARepository extends JpaRepository<EmisionFormaNumerada, UUID> {
    Optional<EmisionFormaNumerada> findBySerieIdAndNumero(UUID serieId, Integer numero);
    Optional<EmisionFormaNumerada> findBySerieIdAndDocumentoTipoAndDocumentoId(
            UUID serieId, String documentoTipo, UUID documentoId);
}
