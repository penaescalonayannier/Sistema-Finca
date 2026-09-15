package com.kynsoft.report.applications.query.report.estadoCuenta.previewXml;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.domain.dto.EstadoCuentaDto;
import com.kynsoft.report.infrastructure.util.XmlParserUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PreviewXmlQueryHandler implements IQueryHandler<PreviewXmlQuery, PreviewXmlResponse> {

    @Override
    public PreviewXmlResponse handle(PreviewXmlQuery query) {
        try {
            List<EstadoCuentaDto> operaciones = XmlParserUtil.parse(query.getXmlContent());
            return new PreviewXmlResponse(operaciones);
        } catch (Exception ex) {
            throw new RuntimeException("Error al procesar XML");
        }
    }
}
