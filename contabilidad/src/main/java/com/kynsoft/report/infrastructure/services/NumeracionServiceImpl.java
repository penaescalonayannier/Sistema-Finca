package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.TipoDocumento;
import com.kynsoft.report.domain.services.INumeracionService;
import com.kynsoft.report.infrastructure.entity.ConfiguracionNumeracion;
import com.kynsoft.report.infrastructure.repository.command.ConfiguracionNumeracionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ConfiguracionNumeracionReadDataJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NumeracionServiceImpl implements INumeracionService {

    private final ConfiguracionNumeracionReadDataJPARepository readRepository;
    private final ConfiguracionNumeracionWriteDataJPARepository writeRepository;

    @Override
    @Transactional
    public String generarSiguienteNumero(UUID fincaId, TipoDocumento tipo) {
        int anioActual = LocalDate.now().getYear();

        // Buscar configuración existente con bloqueo pesimista para evitar condiciones de carrera
        Optional<ConfiguracionNumeracion> configOpt = readRepository
                .findByFincaIdAndTipoAndAnioForUpdate(fincaId, tipo, anioActual);

        ConfiguracionNumeracion config;
        if (configOpt.isPresent()) {
            config = configOpt.get();
        } else {
            // Crear nueva configuración para este año
            config = new ConfiguracionNumeracion();
            config.setId(UUID.randomUUID());
            config.setFincaId(fincaId);
            config.setTipo(tipo);
            config.setPrefijo(tipo.getPrefijo());
            config.setAnio(anioActual);
            config.setUltimoNumero(0);
            config = writeRepository.save(config);
        }

        // Generar siguiente número
        String numero = config.generarSiguienteNumero();
        writeRepository.save(config);

        log.info("Generado número {} para finca {} tipo {}", numero, fincaId, tipo);
        return numero;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer obtenerUltimoNumero(UUID fincaId, TipoDocumento tipo, Integer anio) {
        return readRepository.findByFincaIdAndTipoAndAnio(fincaId, tipo, anio)
                .map(ConfiguracionNumeracion::getUltimoNumero)
                .orElse(0);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarIntegridadSecuencia(UUID fincaId, TipoDocumento tipo, Integer anio) {
        // Esta implementación básica solo verifica que existe la configuración
        // Una implementación completa requeriría verificar contra la tabla de salidas
        return readRepository.findByFincaIdAndTipoAndAnio(fincaId, tipo, anio).isPresent();
    }
}
