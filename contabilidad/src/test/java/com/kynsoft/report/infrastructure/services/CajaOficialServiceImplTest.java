package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.FondoCajaRequest;
import com.kynsoft.report.domain.dto.TipoFondoCaja;
import com.kynsoft.report.domain.dto.Rol;
import com.kynsoft.report.infrastructure.entity.FondoCajaAutorizado;
import com.kynsoft.report.infrastructure.repository.command.*;
import com.kynsoft.report.infrastructure.repository.query.*;
import com.kynsoft.report.infrastructure.security.TenantContext;
import com.kynsoft.report.infrastructure.security.TenantInfo;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CajaOficialServiceImplTest {
    @Test
    void creaFondoAutorizadoPorFincaYTipo() {
        FondoCajaAutorizadoWriteDataJPARepository fondoWrite = mock(FondoCajaAutorizadoWriteDataJPARepository.class);
        FondoCajaAutorizadoReadDataJPARepository fondoRead = mock(FondoCajaAutorizadoReadDataJPARepository.class);
        CajaOficialServiceImpl service = new CajaOficialServiceImpl(fondoWrite, fondoRead,
                mock(ActaResponsabilidadCajaWriteDataJPARepository.class), mock(ActaResponsabilidadCajaReadDataJPARepository.class),
                mock(IncidenciaArqueoCajaWriteDataJPARepository.class), mock(IncidenciaArqueoCajaReadDataJPARepository.class),
                mock(ArqueoCajaReadDataJPARepository.class), mock(MovimientoCajaReadDataJPARepository.class));
        UUID fincaId = UUID.randomUUID();
        when(fondoWrite.findForUpdate(fincaId, TipoFondoCaja.CAMBIO)).thenReturn(java.util.Optional.empty());
        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());
        var response = service.guardarFondo(FondoCajaRequest.builder().fincaId(fincaId).tipo(TipoFondoCaja.CAMBIO).importeAutorizado(500d).build());
        TenantContext.clear();
        assertEquals(500d, response.getImporteAutorizado());
        verify(fondoWrite).save(any(FondoCajaAutorizado.class));
    }
}
