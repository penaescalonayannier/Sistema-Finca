package com.kynsoft.report.applications.query.report.estadoCuenta.previewXml;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class PreviewXmlQuery implements IQuery {

    private final String xmlContent;

    public PreviewXmlQuery(String xmlContent) {
        this.xmlContent = xmlContent;
    }
}
