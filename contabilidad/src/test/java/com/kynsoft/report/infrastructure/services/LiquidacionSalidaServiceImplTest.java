package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.AplicacionLiquidacionSalidaDto;
import com.kynsoft.report.domain.dto.FormaPago;
import com.kynsoft.report.domain.dto.LiquidarSalidaRequest;
import com.kynsoft.report.domain.dto.DenominacionCajaDto;
import com.kynsoft.report.domain.dto.CambioDenominacionesCajaRequest;
import com.kynsoft.report.domain.dto.TipoMovimientoCaja;
import com.kynsoft.report.infrastructure.entity.DeudaTrabajador;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.entity.FincaProducto;
import com.kynsoft.report.infrastructure.entity.ItemSalida;
import com.kynsoft.report.infrastructure.entity.LiquidacionItemSalida;
import com.kynsoft.report.infrastructure.entity.MovimientoCaja;
import com.kynsoft.report.infrastructure.entity.SaldoCajaDenominacion;
import com.kynsoft.report.infrastructure.entity.Salida;
import com.kynsoft.report.infrastructure.repository.command.DeudaTrabajadorDetalleWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.DeudaTrabajadorWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.EntregaBancoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.ItemSalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.LiquidacionItemSalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.LiquidacionSalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.MovimientoCajaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.MovimientoCajaDenominacionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.SaldoCajaDenominacionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.SalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.DeudaTrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.EntregaBancoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.LiquidacionItemSalidaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.MovimientoCajaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.MovimientoCajaDenominacionReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.SaldoCajaDenominacionReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.SalidaReadDataJPARepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

class LiquidacionSalidaServiceImplTest {

    @Test
    void cambiaDenominacionesSinModificarElImporteDeCaja() {
        SalidaReadDataJPARepository salidaRead = mock(SalidaReadDataJPARepository.class);
        SalidaWriteDataJPARepository salidaWrite = mock(SalidaWriteDataJPARepository.class);
        ItemSalidaWriteDataJPARepository itemWrite = mock(ItemSalidaWriteDataJPARepository.class);
        LiquidacionSalidaWriteDataJPARepository liquidacionWrite = mock(LiquidacionSalidaWriteDataJPARepository.class);
        LiquidacionItemSalidaWriteDataJPARepository aplicacionWrite = mock(LiquidacionItemSalidaWriteDataJPARepository.class);
        LiquidacionItemSalidaReadDataJPARepository aplicacionRead = mock(LiquidacionItemSalidaReadDataJPARepository.class);
        DeudaTrabajadorReadDataJPARepository deudaRead = mock(DeudaTrabajadorReadDataJPARepository.class);
        DeudaTrabajadorWriteDataJPARepository deudaWrite = mock(DeudaTrabajadorWriteDataJPARepository.class);
        DeudaTrabajadorDetalleWriteDataJPARepository detalleWrite = mock(DeudaTrabajadorDetalleWriteDataJPARepository.class);
        MovimientoCajaReadDataJPARepository cajaRead = mock(MovimientoCajaReadDataJPARepository.class);
        MovimientoCajaWriteDataJPARepository cajaWrite = mock(MovimientoCajaWriteDataJPARepository.class);
        MovimientoCajaDenominacionWriteDataJPARepository cajaDenominacionWrite = mock(MovimientoCajaDenominacionWriteDataJPARepository.class);
        MovimientoCajaDenominacionReadDataJPARepository cajaDenominacionRead = mock(MovimientoCajaDenominacionReadDataJPARepository.class);
        SaldoCajaDenominacionWriteDataJPARepository saldoDenominacionWrite = mock(SaldoCajaDenominacionWriteDataJPARepository.class);
        SaldoCajaDenominacionReadDataJPARepository saldoDenominacionRead = mock(SaldoCajaDenominacionReadDataJPARepository.class);
        EntregaBancoWriteDataJPARepository entregaWrite = mock(EntregaBancoWriteDataJPARepository.class);
        EntregaBancoReadDataJPARepository entregaRead = mock(EntregaBancoReadDataJPARepository.class);
        LiquidacionSalidaServiceImpl service = new LiquidacionSalidaServiceImpl(salidaRead, salidaWrite, itemWrite,
                liquidacionWrite, aplicacionWrite, aplicacionRead, deudaRead, deudaWrite, detalleWrite,
                cajaRead, cajaWrite, cajaDenominacionWrite, cajaDenominacionRead, saldoDenominacionWrite,
                saldoDenominacionRead, entregaWrite, entregaRead, mock(AuditoriaTransaccionalService.class));

        UUID fincaId = UUID.randomUUID();
        SaldoCajaDenominacion billete100 = new SaldoCajaDenominacion();
        billete100.setFincaId(fincaId); billete100.setDenominacion(100); billete100.setCantidad(1);
        when(saldoDenominacionWrite.findByFincaIdAndDenominacionForUpdate(fincaId, 100))
                .thenReturn(Optional.of(billete100));

        UUID id = service.cambiarDenominaciones(CambioDenominacionesCajaRequest.builder().fincaId(fincaId)
                .denominacionesEntregadas(List.of(DenominacionCajaDto.builder().denominacion(100).cantidad(1).build()))
                .denominacionesRecibidas(List.of(DenominacionCajaDto.builder().denominacion(50).cantidad(2).build()))
                .build());

        assertNotNull(id);
        ArgumentCaptor<MovimientoCaja> movimiento = ArgumentCaptor.forClass(MovimientoCaja.class);
        verify(cajaWrite, times(2)).save(movimiento.capture());
        assertEquals(TipoMovimientoCaja.CAMBIO_DENOMINACION, movimiento.getValue().getTipo());
        assertEquals(0d, movimiento.getValue().getImporte());
        assertEquals(0, billete100.getCantidad());
        verify(cajaDenominacionWrite, times(2)).save(any());
    }

    @Test
    void liquidaItemTrabajadorEnEfectivoSinDuplicarLaDeuda() {
        SalidaReadDataJPARepository salidaRead = mock(SalidaReadDataJPARepository.class);
        SalidaWriteDataJPARepository salidaWrite = mock(SalidaWriteDataJPARepository.class);
        ItemSalidaWriteDataJPARepository itemWrite = mock(ItemSalidaWriteDataJPARepository.class);
        LiquidacionSalidaWriteDataJPARepository liquidacionWrite = mock(LiquidacionSalidaWriteDataJPARepository.class);
        LiquidacionItemSalidaWriteDataJPARepository aplicacionWrite = mock(LiquidacionItemSalidaWriteDataJPARepository.class);
        LiquidacionItemSalidaReadDataJPARepository aplicacionRead = mock(LiquidacionItemSalidaReadDataJPARepository.class);
        DeudaTrabajadorReadDataJPARepository deudaRead = mock(DeudaTrabajadorReadDataJPARepository.class);
        DeudaTrabajadorWriteDataJPARepository deudaWrite = mock(DeudaTrabajadorWriteDataJPARepository.class);
        DeudaTrabajadorDetalleWriteDataJPARepository detalleWrite = mock(DeudaTrabajadorDetalleWriteDataJPARepository.class);
        MovimientoCajaReadDataJPARepository cajaRead = mock(MovimientoCajaReadDataJPARepository.class);
        MovimientoCajaWriteDataJPARepository cajaWrite = mock(MovimientoCajaWriteDataJPARepository.class);
        MovimientoCajaDenominacionWriteDataJPARepository cajaDenominacionWrite = mock(MovimientoCajaDenominacionWriteDataJPARepository.class);
        MovimientoCajaDenominacionReadDataJPARepository cajaDenominacionRead = mock(MovimientoCajaDenominacionReadDataJPARepository.class);
        SaldoCajaDenominacionWriteDataJPARepository saldoDenominacionWrite = mock(SaldoCajaDenominacionWriteDataJPARepository.class);
        SaldoCajaDenominacionReadDataJPARepository saldoDenominacionRead = mock(SaldoCajaDenominacionReadDataJPARepository.class);
        EntregaBancoWriteDataJPARepository entregaWrite = mock(EntregaBancoWriteDataJPARepository.class);
        EntregaBancoReadDataJPARepository entregaRead = mock(EntregaBancoReadDataJPARepository.class);
        LiquidacionSalidaServiceImpl service = new LiquidacionSalidaServiceImpl(salidaRead, salidaWrite, itemWrite,
                liquidacionWrite, aplicacionWrite, aplicacionRead, deudaRead, deudaWrite, detalleWrite,
                cajaRead, cajaWrite, cajaDenominacionWrite, cajaDenominacionRead, saldoDenominacionWrite,
                saldoDenominacionRead, entregaWrite, entregaRead, mock(AuditoriaTransaccionalService.class));

        UUID salidaId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        UUID trabajadorId = UUID.randomUUID();
        Finca finca = new Finca(); finca.setId(UUID.randomUUID());
        FincaProducto fincaProducto = new FincaProducto(); fincaProducto.setFinca(finca);
        ItemSalida item = new ItemSalida();
        item.setId(itemId); item.setSalidaId(salidaId); item.setTrabajadorId(trabajadorId);
        item.setCantidad(2d); item.setPrecio(5d);
        Salida salida = new Salida();
        salida.setId(salidaId); salida.setActivo(true); salida.setNumero("VAL-1");
        salida.setFincaProducto(fincaProducto); salida.setItems(List.of(item));
        DeudaTrabajador deuda = new DeudaTrabajador(); deuda.setImporte(10d);

        when(salidaRead.findByIdWithDetails(salidaId)).thenReturn(Optional.of(salida));
        when(itemWrite.findByIdForUpdate(itemId)).thenReturn(Optional.of(item));
        when(aplicacionRead.totalCobradoByItemSalidaId(itemId)).thenReturn(0d);
        when(deudaRead.findByTrabajadorId(trabajadorId)).thenReturn(Optional.of(deuda));

        UUID id = service.liquidar(LiquidarSalidaRequest.builder().salidaId(salidaId)
                .aplicaciones(List.of(AplicacionLiquidacionSalidaDto.builder().itemSalidaId(itemId)
                        .importe(10d).formaPago(FormaPago.EFECTIVO)
                        .denominaciones(List.of(DenominacionCajaDto.builder().denominacion(5).cantidad(2).build()))
                        .build())).build());

        assertNotNull(id);
        assertEquals(0d, deuda.getImporte());
        assertEquals(Boolean.TRUE, item.getPagado());
        assertEquals(Boolean.TRUE, salida.getPagado());
        verify(aplicacionWrite).save(any(LiquidacionItemSalida.class));
        verify(deudaWrite).save(deuda);
        verify(cajaWrite).save(any(MovimientoCaja.class));
    }
}
