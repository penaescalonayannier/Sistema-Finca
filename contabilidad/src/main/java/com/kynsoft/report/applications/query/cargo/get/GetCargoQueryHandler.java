package com.kynsoft.report.applications.query.cargo.get;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.CargoResponse;
import com.kynsoft.report.domain.dto.CargoDto;
import com.kynsoft.report.domain.services.ICargoService;
import org.springframework.stereotype.Component;

@Component
public class GetCargoQueryHandler implements IQueryHandler<GetCargoQuery, CargoResponse> {

    private final ICargoService serviceImpl;

    public GetCargoQueryHandler(ICargoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public CargoResponse handle(GetCargoQuery query) {
        CargoDto dto = serviceImpl.findById(query.getId());
        return new CargoResponse(dto);
    }
}
