package com.kynsoft.report.applications.query.fincaproducto.getall;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.FincaProductoResponse;
import com.kynsoft.report.infrastructure.entity.FincaProducto;
import com.kynsoft.report.infrastructure.repository.query.FincaProductoReadDataJPARepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class GetAllFincaProductoQueryHandler
        implements IQueryHandler<GetAllFincaProductoQuery, PaginatedResponse> {

    private final FincaProductoReadDataJPARepository repositoryQuery;

    @Override
    public PaginatedResponse handle(GetAllFincaProductoQuery query) {
        GenericSpecificationsBuilder<FincaProducto> specifications
                = new GenericSpecificationsBuilder<>(query.getFilter());

        Page<FincaProducto> data = repositoryQuery.findAll(specifications, query.getPageable());

        List<FincaProductoResponse> responses = data.getContent().stream()
                .map(fp -> {
                    FincaProductoResponse response = new FincaProductoResponse();
                    response.setId(fp.getId());
                    response.setFincaId(fp.getFinca().getId());
                    response.setFincaCode(fp.getFinca().getCode());
                    response.setFincaName(fp.getFinca().getName());
                    response.setProductoId(fp.getProducto().getId());
                    response.setProductoCode(fp.getProducto().getCode());
                    response.setProductoName(fp.getProducto().getName());
                    response.setProductoPrice(fp.getProducto().getPrice());
                    response.setProductoTipo(fp.getProducto().getTipoProducto());
                    response.setStock(fp.getStock());
                    response.setStockMinimo(fp.getStockMinimo());
                    response.setActivo(fp.getActivo());
                    // Calcular alerta de stock bajo
                    boolean alerta = fp.getStockMinimo() != null && fp.getStockMinimo() > 0
                            && fp.getStock() != null && fp.getStock() <= fp.getStockMinimo();
                    response.setAlertaStockBajo(alerta);
                    return response;
                })
                .collect(Collectors.toList());

        return new PaginatedResponse(
                responses,
                data.getTotalPages(),
                data.getNumberOfElements(),
                data.getTotalElements(),
                data.getSize(),
                data.getNumber()
        );
    }
}
