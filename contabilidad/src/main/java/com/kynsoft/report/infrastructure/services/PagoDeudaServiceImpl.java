package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.report.domain.dto.DeudaTrabajadorDetalleDto;
import com.kynsoft.report.domain.dto.PagoDeudaDto;
import com.kynsoft.report.domain.dto.TipoDocumento;
import com.kynsoft.report.domain.dto.TipoMovimiento;
import com.kynsoft.report.domain.dto.AlcanceFormaNumerada;
import com.kynsoft.report.domain.dto.EmitirFormaNumeradaRequest;
import com.kynsoft.report.domain.services.IDeudaTrabajadorDetalleService;
import com.kynsoft.report.domain.services.INumeracionService;
import com.kynsoft.report.domain.services.IRegistroFormasNumeradasService;
import com.kynsoft.report.domain.services.IPagoDeudaService;
import com.kynsoft.report.infrastructure.entity.DeudaTrabajador;
import com.kynsoft.report.infrastructure.entity.PagoDeuda;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.repository.command.DeudaTrabajadorWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.PagoDeudaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.DeudaTrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.PagoDeudaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PagoDeudaServiceImpl implements IPagoDeudaService {

    private final PagoDeudaWriteDataJPARepository pagoWriteRepository;
    private final PagoDeudaReadDataJPARepository pagoReadRepository;
    private final DeudaTrabajadorReadDataJPARepository deudaReadRepository;
    private final DeudaTrabajadorWriteDataJPARepository deudaWriteRepository;
    private final TrabajadorReadDataJPARepository trabajadorReadRepository;
    private final IDeudaTrabajadorDetalleService detalleService;
    private final INumeracionService numeracionService;
    private final IRegistroFormasNumeradasService registroFormasNumeradasService;

    public PagoDeudaServiceImpl(PagoDeudaWriteDataJPARepository pagoWriteRepository,
                                 PagoDeudaReadDataJPARepository pagoReadRepository,
                                 DeudaTrabajadorReadDataJPARepository deudaReadRepository,
                                 DeudaTrabajadorWriteDataJPARepository deudaWriteRepository,
                                 TrabajadorReadDataJPARepository trabajadorReadRepository,
                                 IDeudaTrabajadorDetalleService detalleService,
                                 INumeracionService numeracionService,
                                 IRegistroFormasNumeradasService registroFormasNumeradasService) {
        this.pagoWriteRepository = pagoWriteRepository;
        this.pagoReadRepository = pagoReadRepository;
        this.deudaReadRepository = deudaReadRepository;
        this.deudaWriteRepository = deudaWriteRepository;
        this.trabajadorReadRepository = trabajadorReadRepository;
        this.detalleService = detalleService;
        this.numeracionService = numeracionService;
        this.registroFormasNumeradasService = registroFormasNumeradasService;
    }

    @Override
    @Transactional
    public UUID registrarPago(PagoDeudaDto dto) {
        // Buscar la deuda del trabajador
        DeudaTrabajador deuda = deudaReadRepository.findByTrabajadorId(dto.getTrabajadorId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("trabajadorId", "El trabajador no tiene deuda registrada."))));

        // Validar que el monto no sea mayor a la deuda
        if (dto.getMonto() > deuda.getImporte()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("monto", "El monto del pago no puede ser mayor a la deuda actual.")));
        }

        // Obtener el trabajador para saber su finca
        Trabajador trabajador = trabajadorReadRepository.findById(dto.getTrabajadorId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("trabajadorId", "Trabajador no encontrado."))));

        UUID fincaId = trabajador.getFincaId();

        UUID pagoId = UUID.randomUUID();
        // El recibo se emite en su propia forma y queda inscrito en el libro documental.
        String numeroRecibo = registroFormasNumeradasService.emitir(new EmitirFormaNumeradaRequest(
                "RECIBO_COBRO", AlcanceFormaNumerada.FINCA, fincaId, java.time.LocalDate.now(),
                "PAGO_DEUDA", pagoId, TenantContext.getUsuarioId())).getNumeroFormateado();

        // Capturar saldos
        Double saldoAnterior = deuda.getImporte();
        Double saldoNuevo = saldoAnterior - dto.getMonto();

        // Crear el registro de pago con todos los campos
        PagoDeudaDto pagoDto = PagoDeudaDto.builder()
                .id(pagoId)
                .trabajadorId(dto.getTrabajadorId())
                .monto(dto.getMonto())
                .formaPago(dto.getFormaPago())
                .referenciaBancaria(dto.getReferenciaBancaria())
                .fecha(LocalDateTime.now())
                .numeroRecibo(numeroRecibo)
                .saldoAnterior(saldoAnterior)
                .saldoNuevo(saldoNuevo)
                .concepto(dto.getConcepto() != null ? dto.getConcepto() : "Pago de deuda")
                .fincaId(fincaId)
                .build();

        PagoDeuda pago = new PagoDeuda(pagoDto);
        pagoWriteRepository.save(pago);

        // Disminuir la deuda
        deuda.setImporte(saldoNuevo);
        deudaWriteRepository.save(deuda);

        // Registrar en la tabla de auditoría
        DeudaTrabajadorDetalleDto detalleDto = DeudaTrabajadorDetalleDto.builder()
                .id(UUID.randomUUID())
                .trabajadorId(dto.getTrabajadorId())
                .importe(dto.getMonto())
                .fecha(LocalDateTime.now())
                .activo(true)
                .pagado(true)
                .tipoMovimiento(TipoMovimiento.PAGO)
                .formaPago(dto.getFormaPago())
                .referenciaBancaria(dto.getReferenciaBancaria())
                .build();
        detalleService.registrar(detalleDto);

        // FIFO: Marcar como pagadas las compras más antiguas que cubre este pago
        aplicarPagoFIFO(dto.getTrabajadorId(), dto.getMonto());

        return pago.getId();
    }

    @Override
    public PagoDeudaDto findById(UUID id) {
        return pagoReadRepository.findById(id)
                .map(PagoDeuda::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Pago no encontrado."))));
    }

    @Override
    public List<PagoDeudaDto> findByTrabajadorId(UUID trabajadorId) {
        return pagoReadRepository.findByTrabajadorIdOrderByFechaDesc(trabajadorId)
                .stream()
                .map(PagoDeuda::toAggregate)
                .collect(Collectors.toList());
    }

    /**
     * Aplica el pago usando FIFO (First In, First Out).
     * Las compras más antiguas se marcan como pagadas primero.
     */
    private void aplicarPagoFIFO(UUID trabajadorId, Double montoPago) {
        // Obtener compras no pagadas ordenadas por fecha (más antiguas primero)
        List<DeudaTrabajadorDetalleDto> comprasNoPagadas =
                detalleService.findComprasNoPagadasByTrabajadorId(trabajadorId);

        Double montoRestante = montoPago;

        for (DeudaTrabajadorDetalleDto compra : comprasNoPagadas) {
            if (montoRestante <= 0) {
                break; // Ya no hay más dinero para aplicar
            }

            Double importeCompra = compra.getImporte();

            if (montoRestante >= importeCompra) {
                // El pago cubre completamente esta compra
                detalleService.marcarComoPagado(compra.getId());
                montoRestante -= importeCompra;
            }
            // Si montoRestante < importeCompra, es un pago parcial
            // La compra queda sin marcar como pagada (se pagará con futuros pagos)
        }
    }
}
