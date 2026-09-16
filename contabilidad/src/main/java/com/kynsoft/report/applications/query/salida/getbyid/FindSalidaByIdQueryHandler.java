package com.kynsoft.report.applications.query.salida.getbyid;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.ItemSalidaResponse;
import com.kynsoft.report.applications.query.responseObject.SalidaResponse;
import com.kynsoft.report.domain.dto.SalidaDto;
import com.kynsoft.report.domain.services.ISalidaService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FindSalidaByIdQueryHandler implements IQueryHandler<FindSalidaByIdQuery, SalidaResponse> {

    private final ISalidaService service;

    public FindSalidaByIdQueryHandler(ISalidaService service) {
        this.service = service;
    }

    @Override
    public SalidaResponse handle(FindSalidaByIdQuery query) {
        SalidaDto dto = service.findById(query.getId());

        // Mapear items
        List<ItemSalidaResponse> items = dto.getItems() != null
                ? dto.getItems().stream()
                    .map(item -> new ItemSalidaResponse(
                            item.getId(),
                            item.getSalidaId(),
                            item.getFincaProductoId(),
                            item.getAlmacenFincaProductoId(),
                            item.getProductoCode(),
                            item.getProductoName(),
                            item.getUnidadMedida(),
                            item.getTrabajadorId(),
                            item.getTrabajadorNombre(),
                            item.getCantidad(),
                            item.getPrecio()
                    ))
                    .collect(Collectors.toList())
                : null;

        return new SalidaResponse(
                dto.getId(),
                dto.getTipo(),
                dto.getDestino(),
                dto.getNumero(),
                dto.getFincaProductoId(),
                dto.getFincaCode(),
                dto.getFincaName(),
                dto.getProductoCode(),
                dto.getProductoName(),
                dto.getStockActual(),
                dto.getFecha(),
                dto.getObservaciones(),
                dto.getCantidadTotal() != null ? dto.getCantidadTotal().doubleValue() : 0.0,
                items
        );
    }
}
