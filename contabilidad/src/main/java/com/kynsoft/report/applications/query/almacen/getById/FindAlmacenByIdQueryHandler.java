package com.kynsoft.report.applications.query.almacen.getById;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.AlmacenResponse;
import com.kynsoft.report.domain.dto.AlmacenDto;
import com.kynsoft.report.domain.services.IAlmacenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FindAlmacenByIdQueryHandler implements IQueryHandler<FindAlmacenByIdQuery, AlmacenResponse> {

    private final IAlmacenService serviceImpl;

    @Override
    public AlmacenResponse handle(FindAlmacenByIdQuery query) {
        AlmacenDto dto = serviceImpl.findById(query.getId());
        return new AlmacenResponse(dto);
    }
}
