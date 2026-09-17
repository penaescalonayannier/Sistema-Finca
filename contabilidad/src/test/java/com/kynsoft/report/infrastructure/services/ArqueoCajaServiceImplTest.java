package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.CerrarArqueoCajaRequest;
import com.kynsoft.report.domain.dto.CrearArqueoCajaRequest;
import com.kynsoft.report.domain.dto.DenominacionCajaDto;
import com.kynsoft.report.domain.dto.EstadoArqueoCaja;
import com.kynsoft.report.domain.dto.TipoArqueoCaja;
import com.kynsoft.report.infrastructure.entity.ArqueoCaja;
import com.kynsoft.report.infrastructure.entity.ArqueoCajaDenominacion;
import com.kynsoft.report.infrastructure.entity.SaldoCajaDenominacion;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.repository.command.ArqueoCajaDenominacionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.ArqueoCajaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ArqueoCajaDenominacionReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ArqueoCajaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.MovimientoCajaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.SaldoCajaDenominacionReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantContext;
import com.kynsoft.report.infrastructure.security.TenantInfo;
import com.kynsoft.report.domain.dto.Rol;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ArqueoCajaServiceImplTest {

    @Test
    void abreArqueoTotalConSnapshotSinModificarCaja() {
        ArqueoCajaWriteDataJPARepository arqueoWrite = mock(ArqueoCajaWriteDataJPARepository.class);
        ArqueoCajaDenominacionWriteDataJPARepository detalleWrite = mock(ArqueoCajaDenominacionWriteDataJPARepository.class);
        ArqueoCajaReadDataJPARepository arqueoRead = mock(ArqueoCajaReadDataJPARepository.class);
        ArqueoCajaDenominacionReadDataJPARepository detalleRead = mock(ArqueoCajaDenominacionReadDataJPARepository.class);
        SaldoCajaDenominacionReadDataJPARepository saldoRead = mock(SaldoCajaDenominacionReadDataJPARepository.class);
        MovimientoCajaReadDataJPARepository movimientoRead = mock(MovimientoCajaReadDataJPARepository.class);
        FincaReadDataJPARepository fincaRead = mock(FincaReadDataJPARepository.class);
        ArqueoCajaServiceImpl service = new ArqueoCajaServiceImpl(arqueoWrite, detalleWrite, arqueoRead, detalleRead,
                saldoRead, movimientoRead, fincaRead, mock(AuditoriaTransaccionalService.class));
        UUID fincaId = UUID.randomUUID();
        SaldoCajaDenominacion billete = new SaldoCajaDenominacion();
        billete.setDenominacion(100); billete.setCantidad(2);
        when(arqueoRead.existsByFincaIdAndEstado(fincaId, EstadoArqueoCaja.ABIERTO)).thenReturn(false);
        Finca finca = new Finca(); finca.setId(fincaId); finca.setActivo(true);
        when(fincaRead.findById(fincaId)).thenReturn(Optional.of(finca));
        when(saldoRead.findByFincaIdOrderByDenominacionAsc(fincaId)).thenReturn(List.of(billete));
        when(movimientoRead.saldoByFincaId(fincaId)).thenReturn(200d);
        when(arqueoWrite.siguienteNumero()).thenReturn(7L);

        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());
        UUID id = service.crear(CrearArqueoCajaRequest.builder().fincaId(fincaId).contadorResponsable("Contadora").build());
        TenantContext.clear();

        assertNotNull(id);
        ArgumentCaptor<ArqueoCaja> arqueo = ArgumentCaptor.forClass(ArqueoCaja.class);
        verify(arqueoWrite).save(arqueo.capture());
        assertEquals(7L, arqueo.getValue().getNumero());
        assertEquals(TipoArqueoCaja.TOTAL, arqueo.getValue().getTipo());
        assertEquals(200d, arqueo.getValue().getTotalEsperado());
        verify(detalleWrite, times(12)).save(any(ArqueoCajaDenominacion.class));
    }

    @Test
    void cierraArqueoConDiferenciaSinAjustarCaja() {
        ArqueoCajaWriteDataJPARepository arqueoWrite = mock(ArqueoCajaWriteDataJPARepository.class);
        ArqueoCajaDenominacionWriteDataJPARepository detalleWrite = mock(ArqueoCajaDenominacionWriteDataJPARepository.class);
        ArqueoCajaReadDataJPARepository arqueoRead = mock(ArqueoCajaReadDataJPARepository.class);
        ArqueoCajaDenominacionReadDataJPARepository detalleRead = mock(ArqueoCajaDenominacionReadDataJPARepository.class);
        SaldoCajaDenominacionReadDataJPARepository saldoRead = mock(SaldoCajaDenominacionReadDataJPARepository.class);
        MovimientoCajaReadDataJPARepository movimientoRead = mock(MovimientoCajaReadDataJPARepository.class);
        FincaReadDataJPARepository fincaRead = mock(FincaReadDataJPARepository.class);
        ArqueoCajaServiceImpl service = new ArqueoCajaServiceImpl(arqueoWrite, detalleWrite, arqueoRead, detalleRead,
                saldoRead, movimientoRead, fincaRead, mock(AuditoriaTransaccionalService.class));
        UUID id = UUID.randomUUID();
        ArqueoCaja arqueo = new ArqueoCaja();
        arqueo.setId(id); arqueo.setEstado(EstadoArqueoCaja.ABIERTO); arqueo.setTotalEsperado(10d);
        arqueo.setFincaId(UUID.randomUUID());
        ArqueoCajaDenominacion detalle = new ArqueoCajaDenominacion();
        detalle.setArqueoCajaId(id); detalle.setDenominacion(5); detalle.setCantidadEsperada(2);
        when(arqueoWrite.findByIdForUpdate(id)).thenReturn(Optional.of(arqueo));
        when(detalleRead.findByArqueoCajaIdOrderByDenominacionAsc(id)).thenReturn(List.of(detalle));

        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());
        service.cerrar(id, CerrarArqueoCajaRequest.builder().observaciones("Faltante informado")
                .conteoFisico(List.of(DenominacionCajaDto.builder().denominacion(5).cantidad(1).build())).build());
        TenantContext.clear();

        assertEquals(EstadoArqueoCaja.CERRADO, arqueo.getEstado());
        assertEquals(5d, arqueo.getTotalFisico());
        assertEquals(-5d, arqueo.getDiferencia());
        assertEquals(1, detalle.getCantidadFisica());
        verify(arqueoWrite).save(arqueo);
        verify(detalleWrite).save(detalle);
    }
}
