package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.TipoDocumento;
import com.kynsoft.report.domain.services.INumeracionService;
import com.kynsoft.report.infrastructure.entity.ConfiguracionNumeracion;
import com.kynsoft.report.infrastructure.repository.command.ConfiguracionNumeracionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ConfiguracionNumeracionReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.SalidaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ProduccionTerminadaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TransferenciaAlmacenReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.InformeRecepcionReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantValidator;
import com.kynsoft.report.domain.dto.EstadoConsecutivoDocumentoDto;
import com.kynsoft.report.domain.dto.TipoSalida;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class NumeracionServiceImpl implements INumeracionService {

    private final ConfiguracionNumeracionReadDataJPARepository readRepository;
    private final ConfiguracionNumeracionWriteDataJPARepository writeRepository;
    private final SalidaReadDataJPARepository salidaReadRepository;
    private final ProduccionTerminadaReadDataJPARepository produccionReadRepository;
    private final TransferenciaAlmacenReadDataJPARepository transferenciaReadRepository;
    private final InformeRecepcionReadDataJPARepository informeRecepcionReadRepository;

    @PersistenceContext(unitName = "WriteDB")
    private EntityManager writeEntityManager;

    @Override
    @Transactional(transactionManager = "writeTransactionManager")
    public String generarSiguienteNumero(UUID fincaId, TipoDocumento tipo) {
        if (fincaId == null || tipo == null) {
            throw new IllegalArgumentException("Finca y tipo de documento son obligatorios para generar un consecutivo.");
        }
        int anioActual = LocalDate.now().getYear();

        // El bloqueo de fila protege una secuencia existente. Para el primer
        // documento aún no hay fila que bloquear; el advisory lock por
        // finca/tipo/año evita que dos transacciones creen la misma secuencia.
        writeEntityManager.createNativeQuery("SELECT pg_advisory_xact_lock(hashtext(:clave))")
                .setParameter("clave", fincaId + ":" + tipo.name() + ":" + anioActual)
                .getSingleResult();

        // La lectura con bloqueo debe ocurrir en el EntityManager de escritura;
        // una réplica/read-only no puede garantizar exclusión mutua.
        Optional<ConfiguracionNumeracion> configOpt = writeRepository
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
        TenantValidator.validateReadAccess(fincaId);
        return estado(fincaId, tipo, anio).isIntegridad();
    }

    @Override
    @Transactional(readOnly = true, transactionManager = "readTransactionManager")
    public List<EstadoConsecutivoDocumentoDto> obtenerEstadoDocumentos(UUID fincaId, Integer anio) {
        if (fincaId == null || anio == null || anio < 2000 || anio > 9999) {
            throw new IllegalArgumentException("La finca y un año válido son obligatorios.");
        }
        TenantValidator.validateReadAccess(fincaId);
        return List.of(TipoDocumento.VALE, TipoDocumento.FACTURA, TipoDocumento.PRODUCCION,
                        TipoDocumento.TRANSFERENCIA_ALMACEN, TipoDocumento.RECEPCION).stream()
                .map(tipo -> estado(fincaId, tipo, anio))
                .toList();
    }

    private EstadoConsecutivoDocumentoDto estado(UUID fincaId, TipoDocumento tipo, Integer anio) {
        ConfiguracionNumeracion config = readRepository.findByFincaIdAndTipoAndAnio(fincaId, tipo, anio).orElse(null);
        int ultimo = config == null || config.getUltimoNumero() == null ? 0 : config.getUltimoNumero();
        String prefijo = config == null ? tipo.getPrefijo() : config.getPrefijo();
        List<String> numeros = numerosDocumentos(fincaId, tipo, anio);
        boolean integridad = secuenciaCompleta(numeros, prefijo, anio, ultimo);
        return EstadoConsecutivoDocumentoDto.builder()
                .tipo(tipo).prefijo(prefijo).anio(anio).ultimoNumero(ultimo)
                .proximoNumero(String.format("%s-%d-%05d", prefijo, anio, ultimo + 1))
                .cantidadDocumentos(numeros.size()).integridad(integridad).build();
    }

    private List<String> numerosDocumentos(UUID fincaId, TipoDocumento tipo, int anio) {
        LocalDate inicio = LocalDate.of(anio, 1, 1);
        LocalDate fin = inicio.plusYears(1);
        if (tipo == TipoDocumento.PRODUCCION) {
            return produccionReadRepository.findNumerosDocumentoPorFincaYAnio(fincaId,
                    inicio.atStartOfDay(), fin.atStartOfDay());
        }
        if (tipo == TipoDocumento.TRANSFERENCIA_ALMACEN) {
            return transferenciaReadRepository.findNumerosDocumentoPorFincaYAnio(fincaId,
                    inicio.atStartOfDay(), fin.atStartOfDay());
        }
        if (tipo == TipoDocumento.RECEPCION) {
            return informeRecepcionReadRepository.findNumerosDocumentoPorFincaYAnio(fincaId,
                    inicio.atStartOfDay(), fin.atStartOfDay());
        }
        TipoSalida tipoSalida = tipo == TipoDocumento.VALE ? TipoSalida.VALE : TipoSalida.FACTURA;
        return salidaReadRepository.findNumerosPorFincaTipoYAnio(fincaId, tipoSalida,
                inicio.atStartOfDay(), fin.atStartOfDay());
    }

    private boolean secuenciaCompleta(List<String> numeros, String prefijo, int anio, int ultimo) {
        if (ultimo == 0) return numeros.isEmpty();
        String inicio = prefijo + "-" + anio + "-";
        Set<Integer> secuencia = new HashSet<>();
        for (String numero : numeros) {
            if (numero == null || !numero.startsWith(inicio)) return false;
            try {
                secuencia.add(Integer.parseInt(numero.substring(inicio.length())));
            } catch (NumberFormatException ex) {
                return false;
            }
        }
        if (secuencia.size() != ultimo || numeros.size() != ultimo) return false;
        for (int i = 1; i <= ultimo; i++) if (!secuencia.contains(i)) return false;
        return true;
    }
}
