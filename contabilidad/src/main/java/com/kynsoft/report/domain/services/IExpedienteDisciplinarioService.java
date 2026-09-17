package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.ActualizarExpedienteDisciplinarioRequest;
import com.kynsoft.report.domain.dto.AnularExpedienteDisciplinarioRequest;
import com.kynsoft.report.domain.dto.CrearExpedienteDisciplinarioRequest;
import com.kynsoft.report.domain.dto.ExpedienteDisciplinarioDto;
import com.kynsoft.report.domain.dto.ResolverExpedienteDisciplinarioRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IExpedienteDisciplinarioService {
    UUID crear(CrearExpedienteDisciplinarioRequest request);
    void actualizar(UUID id, ActualizarExpedienteDisciplinarioRequest request);
    void notificar(UUID id);
    void resolver(UUID id, ResolverExpedienteDisciplinarioRequest request);
    void anular(UUID id, AnularExpedienteDisciplinarioRequest request);
    ExpedienteDisciplinarioDto detalle(UUID id);
    List<ExpedienteDisciplinarioDto> listar(UUID fincaId, UUID trabajadorId, LocalDate desde, LocalDate hasta, boolean incluirAnulados);
}
