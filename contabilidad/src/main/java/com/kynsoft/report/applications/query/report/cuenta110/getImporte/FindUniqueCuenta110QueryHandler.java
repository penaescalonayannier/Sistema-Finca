package com.kynsoft.report.applications.query.report.cuenta110.getImporte;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.Cuenta110EfectivoBancoResponse;
import com.kynsoft.report.domain.dto.Cuenta110EfectivoBancoDto;
import com.kynsoft.report.domain.services.ICuenta110EfectivoBancoService;
import org.springframework.stereotype.Component;

@Component
public class FindUniqueCuenta110QueryHandler implements IQueryHandler<FindUniqueCuenta110Query, Cuenta110EfectivoBancoResponse> {

    private final ICuenta110EfectivoBancoService service;

    public FindUniqueCuenta110QueryHandler(ICuenta110EfectivoBancoService service) {
        this.service = service;
    }

    @Override
    public Cuenta110EfectivoBancoResponse handle(FindUniqueCuenta110Query query) {
        Cuenta110EfectivoBancoDto response = service.findUnique();

        return new Cuenta110EfectivoBancoResponse(response);
    }
}
