package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.AplicacionLiquidacionSalidaDto;
import com.kynsoft.report.domain.dto.FormaPago;
import com.kynsoft.report.domain.dto.LiquidarSalidaRequest;
import com.kynsoft.report.infrastructure.entity.DeudaTrabajador;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.entity.FincaProducto;
import com.kynsoft.report.infrastructure.entity.ItemSalida;
import com.kynsoft.report.infrastructure.entity.LiquidacionItemSalida;
import com.kynsoft.report.infrastructure.entity.MovimientoCaja;
import com.kynsoft.report.infrastructure.entity.Salida;
import com.kynsoft.report.infrastructure.repository.command.DeudaTrabajadorDetalleWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.DeudaTrabajadorWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.EntregaBancoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.ItemSalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.LiquidacionItemSalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.LiquidacionSalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.MovimientoCajaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.SalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.DeudaTrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.EntregaBancoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.LiquidacionItemSalidaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.MovimientoCajaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.SalidaReadDataJPARepository;
import org.junit.jupiter.api.Test;

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

class LiquidacionSalidaServiceImplTest {

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
        EntregaBancoWriteDataJPARepository entregaWrite = mock(EntregaBancoWriteDataJPARepository.class);
        EntregaBancoReadDataJPARepository entregaRead = mock(EntregaBancoReadDataJPARepository.class);
        LiquidacionSalidaServiceImpl service = new LiquidacionSalidaServiceImpl(salidaRead, salidaWrite, itemWrite,
                liquidacionWrite, aplicacionWrite, aplicacionRead, deudaRead, deudaWrite, detalleWrite,
                cajaRead, cajaWrite, entregaWrite, entregaRead);

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
                        .importe(10d).formaPago(FormaPago.EFECTIVO).build())).build());

        assertNotNull(id);
        assertEquals(0d, deuda.getImporte());
        assertEquals(Boolean.TRUE, item.getPagado());
        assertEquals(Boolean.TRUE, salida.getPagado());
        verify(aplicacionWrite).save(any(LiquidacionItemSalida.class));
        verify(deudaWrite).save(deuda);
        verify(cajaWrite).save(any(MovimientoCaja.class));
    }
}
