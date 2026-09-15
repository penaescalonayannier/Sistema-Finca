package com.kynsoft.report.applications.query.report.cuenta110.getById;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.Cuenta110EfectivoBancoResponse;
import com.kynsoft.report.domain.dto.Cuenta110EfectivoBancoDto;
import com.kynsoft.report.domain.services.ICuenta110EfectivoBancoService;
import org.springframework.stereotype.Component;

@Component
public class FindCuenta110ByIdQueryHandler implements IQueryHandler<FindCuenta110ByIdQuery, Cuenta110EfectivoBancoResponse> {

    private final ICuenta110EfectivoBancoService service;

    public FindCuenta110ByIdQueryHandler(ICuenta110EfectivoBancoService service) {
        this.service = service;
    }

    @Override
    public Cuenta110EfectivoBancoResponse handle(FindCuenta110ByIdQuery query) {
        Cuenta110EfectivoBancoDto response = service.findById(query.getId());

        return new Cuenta110EfectivoBancoResponse(response);
    }
}
