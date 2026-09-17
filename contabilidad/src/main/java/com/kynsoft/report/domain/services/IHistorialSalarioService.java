package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.HistorialSalarioDto;
import java.util.List;
import java.util.UUID;

public interface IHistorialSalarioService {
    UUID registrar(HistorialSalarioDto salario);
    void anular(UUID id, String motivo);
    List<HistorialSalarioDto> listarPorTrabajador(UUID trabajadorId);
    HistorialSalarioDto vigente(UUID trabajadorId);
}
