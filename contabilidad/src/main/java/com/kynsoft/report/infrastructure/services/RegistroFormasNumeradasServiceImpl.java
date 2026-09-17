package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.AlcanceFormaNumerada;
import com.kynsoft.report.domain.dto.AnularEmisionFormaNumeradaRequest;
import com.kynsoft.report.domain.dto.EmisionFormaNumeradaDto;
import com.kynsoft.report.domain.dto.EmitirFormaNumeradaRequest;
import com.kynsoft.report.domain.dto.EstadoEmisionFormaNumerada;
import com.kynsoft.report.domain.dto.EstadoSerieFormaNumerada;
import com.kynsoft.report.domain.dto.RegistroFormaNumeradaDto;
import com.kynsoft.report.domain.dto.ReinicioConsecutivoForma;
import com.kynsoft.report.domain.services.IRegistroFormasNumeradasService;
import com.kynsoft.report.infrastructure.entity.EmisionFormaNumerada;
import com.kynsoft.report.infrastructure.entity.FormaNumerada;
import com.kynsoft.report.infrastructure.entity.SerieFormaNumerada;
import com.kynsoft.report.infrastructure.repository.command.EmisionFormaNumeradaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.FormaNumeradaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.SerieFormaNumeradaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.EmisionFormaNumeradaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FormaNumeradaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.SerieFormaNumeradaReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantValidator;
import com.kynsoft.report.infrastructure.security.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistroFormasNumeradasServiceImpl implements IRegistroFormasNumeradasService {

    private final FormaNumeradaWriteDataJPARepository formaWriteRepository;
    private final SerieFormaNumeradaWriteDataJPARepository serieWriteRepository;
    private final EmisionFormaNumeradaWriteDataJPARepository emisionWriteRepository;
    private final FormaNumeradaReadDataJPARepository formaReadRepository;
    private final SerieFormaNumeradaReadDataJPARepository serieReadRepository;
    private final EmisionFormaNumeradaReadDataJPARepository emisionReadRepository;

    @PersistenceContext(unitName = "WriteDB")
    private EntityManager writeEntityManager;

    @Override
    @Transactional(transactionManager = "writeTransactionManager")
    public EmisionFormaNumeradaDto emitir(EmitirFormaNumeradaRequest request) {
        validarSolicitudEmision(request);
        validarAccesoFinca(request.getAlcanceTipo(), request.getAlcanceId());

        String codigoForma = request.getCodigoForma().trim().toUpperCase(Locale.ROOT);
        FormaNumerada forma = formaWriteRepository.findByCodigo(codigoForma)
                .filter(formaActual -> Boolean.TRUE.equals(formaActual.getActiva()))
                .orElseThrow(() -> new IllegalArgumentException("La forma numerada no existe o está inactiva."));
        LocalDate fecha = request.getFechaDocumento() == null ? LocalDate.now() : request.getFechaDocumento();
        Integer anio = forma.getReinicio() == ReinicioConsecutivoForma.ANUAL ? fecha.getYear() : null;

        // Protege el caso en que aún no hay una fila de emisión para ese documento.
        writeEntityManager.createNativeQuery("SELECT pg_advisory_xact_lock(hashtext(:clave))")
                .setParameter("clave", codigoForma + ":" + request.getAlcanceTipo() + ":"
                        + request.getAlcanceId() + ":" + anio)
                .getSingleResult();

        SerieFormaNumerada serie = serieWriteRepository.findActivaForUpdate(
                        forma.getId(), request.getAlcanceTipo(), request.getAlcanceId(), anio,
                        EstadoSerieFormaNumerada.ACTIVA)
                .orElseGet(() -> abrirSerieInicial(forma, request.getAlcanceTipo(), request.getAlcanceId(), anio));

        emisionWriteRepository.findBySerieIdAndDocumentoTipoAndDocumentoId(
                        serie.getId(), request.getDocumentoTipo(), request.getDocumentoId())
                .ifPresent(existente -> {
                    throw new IllegalStateException("El documento ya tiene una forma numerada asignada: "
                            + existente.getNumeroFormateado());
                });

        int siguiente = serie.getUltimoNumero() + 1;
        if (serie.getNumeroFinal() != null && siguiente > serie.getNumeroFinal()) {
            throw new IllegalStateException("La serie de la forma numerada agotó su rango autorizado.");
        }
        serie.setUltimoNumero(siguiente);
        serieWriteRepository.save(serie);

        EmisionFormaNumerada emision = new EmisionFormaNumerada();
        emision.setFormaId(forma.getId());
        emision.setSerieId(serie.getId());
        emision.setNumero(siguiente);
        emision.setNumeroFormateado(formatear(forma, serie, siguiente, anio));
        emision.setDocumentoTipo(request.getDocumentoTipo());
        emision.setDocumentoId(request.getDocumentoId());
        emision.setUsuarioEmisorId(request.getUsuarioEmisorId());
        return emisionWriteRepository.save(emision).toDto();
    }

    @Override
    @Transactional(readOnly = true, transactionManager = "readTransactionManager")
    public List<RegistroFormaNumeradaDto> consultar(UUID fincaId, Integer anio) {
        UUID fincaConsulta = resolverFincaConsulta(fincaId);
        List<SerieFormaNumerada> series = serieReadRepository.buscarDisponibles(fincaConsulta, anio);
        Map<UUID, List<SerieFormaNumerada>> seriesPorForma = series.stream()
                .collect(Collectors.groupingBy(SerieFormaNumerada::getFormaId));
        return formaReadRepository.findByActivaTrueOrderByNombreAsc().stream()
                .flatMap(forma -> {
                    List<SerieFormaNumerada> porForma = seriesPorForma.get(forma.getId());
                    if (porForma == null || porForma.isEmpty()) {
                        return java.util.stream.Stream.of(respuestaSinSerie(forma, anio));
                    }
                    return porForma.stream().map(serie -> respuesta(forma, serie));
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true, transactionManager = "readTransactionManager")
    public List<EmisionFormaNumeradaDto> consultarEmisiones(String codigoForma, UUID fincaId, Integer anio) {
        UUID fincaConsulta = resolverFincaConsulta(fincaId);
        return serieReadRepository.buscarDisponibles(fincaConsulta, anio).stream()
                .filter(serie -> formaReadRepository.findById(serie.getFormaId())
                        .map(forma -> forma.getCodigo().equals(codigoForma)).orElse(false))
                .flatMap(serie -> emisionReadRepository.findBySerieIdOrderByNumeroDesc(serie.getId()).stream())
                .map(EmisionFormaNumerada::toDto)
                .toList();
    }

    @Override
    @Transactional(transactionManager = "writeTransactionManager")
    public EmisionFormaNumeradaDto anular(UUID emisionId, AnularEmisionFormaNumeradaRequest request) {
        if (request == null || request.getMotivo() == null || request.getMotivo().isBlank()) {
            throw new IllegalArgumentException("El motivo de anulación es obligatorio.");
        }
        EmisionFormaNumerada emision = emisionWriteRepository.findById(emisionId)
                .orElseThrow(() -> new IllegalArgumentException("Emisión de forma numerada no encontrada."));
        validarAccesoEmision(emision);
        if (emision.getEstado() == EstadoEmisionFormaNumerada.ANULADA) {
            throw new IllegalStateException("La forma numerada ya fue anulada.");
        }
        emision.setEstado(EstadoEmisionFormaNumerada.ANULADA);
        emision.setFechaAnulacion(LocalDateTime.now());
        emision.setUsuarioAnulacionId(request.getUsuarioId());
        emision.setMotivoAnulacion(request.getMotivo().trim());
        return emisionWriteRepository.save(emision).toDto();
    }

    @Override
    @Transactional(transactionManager = "writeTransactionManager")
    public EmisionFormaNumeradaDto registrarReimpresion(UUID emisionId, UUID usuarioId) {
        EmisionFormaNumerada emision = emisionWriteRepository.findById(emisionId)
                .orElseThrow(() -> new IllegalArgumentException("Emisión de forma numerada no encontrada."));
        validarAccesoEmision(emision);
        emision.setTotalReimpresiones(emision.getTotalReimpresiones() + 1);
        emision.setUltimaReimpresionEn(LocalDateTime.now());
        emision.setUltimoUsuarioReimpresionId(usuarioId);
        return emisionWriteRepository.save(emision).toDto();
    }

    private void validarSolicitudEmision(EmitirFormaNumeradaRequest request) {
        if (request == null || request.getCodigoForma() == null || request.getCodigoForma().isBlank()
                || request.getAlcanceTipo() == null || request.getDocumentoTipo() == null
                || request.getDocumentoTipo().isBlank() || request.getDocumentoId() == null) {
            throw new IllegalArgumentException("Forma, ámbito y documento son obligatorios para emitir.");
        }
        if (request.getAlcanceTipo() != AlcanceFormaNumerada.ENTIDAD && request.getAlcanceId() == null) {
            throw new IllegalArgumentException("El identificador del ámbito es obligatorio para esta serie.");
        }
    }

    private void validarAccesoFinca(AlcanceFormaNumerada alcanceTipo, UUID alcanceId) {
        if (alcanceTipo == AlcanceFormaNumerada.FINCA && alcanceId != null) {
            TenantValidator.validateWriteAccess(alcanceId);
        }
    }

    private void validarAccesoEmision(EmisionFormaNumerada emision) {
        serieWriteRepository.findById(emision.getSerieId()).ifPresent(serie -> {
            if (serie.getAlcanceTipo() == AlcanceFormaNumerada.FINCA && serie.getAlcanceId() != null) {
                TenantValidator.validateWriteAccess(serie.getAlcanceId());
            }
        });
    }

    private String formatear(FormaNumerada forma, SerieFormaNumerada serie, int numero, Integer anio) {
        String prefijo = serie.getPrefijo() == null || serie.getPrefijo().isBlank()
                ? forma.getPrefijo() : serie.getPrefijo();
        String secuencia = String.format("%0" + forma.getDigitos() + "d", numero);
        return anio == null ? prefijo + "-" + secuencia : prefijo + "-" + anio + "-" + secuencia;
    }

    private SerieFormaNumerada abrirSerieInicial(FormaNumerada forma, AlcanceFormaNumerada alcanceTipo,
                                                   UUID alcanceId, Integer anio) {
        SerieFormaNumerada serie = new SerieFormaNumerada();
        serie.setFormaId(forma.getId());
        serie.setAlcanceTipo(alcanceTipo);
        serie.setAlcanceId(alcanceId);
        serie.setAnio(anio);
        serie.setPrefijo(forma.getPrefijo());
        serie.setNumeroInicial(1);
        serie.setUltimoNumero(0);
        serie.setEstado(EstadoSerieFormaNumerada.ACTIVA);
        return serieWriteRepository.save(serie);
    }

    private UUID resolverFincaConsulta(UUID fincaId) {
        UUID efectiva = fincaId == null ? TenantContext.getEffectiveFincaId() : fincaId;
        if (efectiva != null) {
            TenantValidator.validateReadAccess(efectiva);
        } else if (!TenantContext.isAdmin()) {
            throw new IllegalStateException("Debe indicar una finca para consultar las formas numeradas.");
        }
        return efectiva;
    }

    private RegistroFormaNumeradaDto respuestaSinSerie(FormaNumerada forma, Integer anio) {
        return RegistroFormaNumeradaDto.builder()
                .tipo(forma.getCodigo()).prefijo(forma.getPrefijo()).anio(anio)
                .ultimoNumero(0).proximoNumero(1).cantidadDocumentos(0).integridad(true)
                .formaCodigo(forma.getCodigo()).formaNombre(forma.getNombre())
                .serie(forma.getReinicio() == ReinicioConsecutivoForma.ANUAL
                        ? forma.getPrefijo() + "-" + anio : forma.getPrefijo())
                .alcanceDescripcion(forma.getAlcancePredeterminado().name())
                .estado("SIN_SERIE").build();
    }

    private RegistroFormaNumeradaDto respuesta(FormaNumerada forma, SerieFormaNumerada serie) {
        List<EmisionFormaNumerada> emisiones = emisionReadRepository.findBySerieIdOrderByNumeroDesc(serie.getId());
        int ultimo = serie.getUltimoNumero() == null ? 0 : serie.getUltimoNumero();
        int inicial = serie.getNumeroInicial() == null ? 1 : serie.getNumeroInicial();
        // V41 importa el último correlativo de documentos anteriores sin
        // fabricar emisiones históricas. Mientras no haya emisiones nuevas,
        // la serie queda identificada como base histórica y no se informa un
        // falso hueco documental.
        boolean baseHistoricaSinLibro = emisiones.isEmpty() && inicial > 1 && ultimo == inicial - 1;
        boolean integridad = baseHistoricaSinLibro
                || emisiones.size() == Math.max(0, ultimo - inicial + 1);
        String prefijo = serie.getPrefijo() == null || serie.getPrefijo().isBlank()
                ? forma.getPrefijo() : serie.getPrefijo();
        String etiquetaSerie = serie.getAnio() == null ? prefijo : prefijo + "-" + serie.getAnio();
        return RegistroFormaNumeradaDto.builder()
                .tipo(forma.getCodigo()).prefijo(prefijo).anio(serie.getAnio())
                .ultimoNumero(ultimo).proximoNumero(ultimo + 1).cantidadDocumentos(emisiones.size())
                .integridad(integridad).formaCodigo(forma.getCodigo()).formaNombre(forma.getNombre())
                .serie(etiquetaSerie).alcanceDescripcion(describirAlcance(serie))
                .estado(baseHistoricaSinLibro ? "HISTORICO_IMPORTADO" : serie.getEstado().name()).build();
    }

    private String describirAlcance(SerieFormaNumerada serie) {
        return serie.getAlcanceId() == null ? serie.getAlcanceTipo().name()
                : serie.getAlcanceTipo().name() + ": " + serie.getAlcanceId();
    }
}
