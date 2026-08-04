package com.kynsoft.report.infrastructure.util;

import com.kynsoft.report.domain.dto.EstadoCuentaDto;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter; // Importar la clase necesaria
import java.util.ArrayList;
import java.util.List;

public class XmlParserUtil {

    // 1. DEFINIR EL FORMATO DE ENTRADA (DD/MM/YYYY)
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // 2. DEFINIR EL FORMATO DE SALIDA (YYYY-MM-DD) para consistencia con la DB/Specification
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static List<EstadoCuentaDto> parse(String xmlContent) throws Exception {
        List<EstadoCuentaDto> dtos = new ArrayList<>();

        // ... (Configuración de DocumentBuilderFactory y DocumentBuilder) ...
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlContent)));

        NodeList nodeList = document.getElementsByTagName("Estado_x0020_de_x0020_Cuenta");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);

            // Lógica para ignorar saldos iniciales/finales
            String observacion = getTagValue(element, "observ");
            if (observacion.contains("Saldo Contable Anterior")
                    || observacion.contains("Saldo Contable Final")
                    || observacion.contains("Saldo Reservado")
                    || observacion.contains("Saldo Sobre Giro")
                    || observacion.contains("Saldo Disponible Final")) {
                continue;
            }

            // OBTENER LA FECHA Y PARSEARLA
            String fechaXml = getTagValue(element, "fecha");
            LocalDate fechaFormateada;

            try {
                // Parseamos la cadena de entrada (DD/MM/YYYY) a un objeto LocalDate
                LocalDate localDate = LocalDate.parse(fechaXml, INPUT_FORMATTER);

                // Convertimos el LocalDate al formato de String YYYY-MM-DD
                fechaFormateada = LocalDate.parse(localDate.format(OUTPUT_FORMATTER));
            } catch (Exception e) {
                // Si la fecha es crítica, maneje la excepción o lance un error aquí
                throw new Exception("Error al procesar la fecha: " + fechaXml + ". Formato esperado dd/MM/yyyy.", e);
            }

            // CREACIÓN DEL DTO
            EstadoCuentaDto dto = EstadoCuentaDto.builder()
                    // LÍNEA MODIFICADA para usar la fecha formateada
                    .fecha(fechaFormateada)
                    .refCorriente(getTagValue(element, "ref_corrie"))
                    .refOrigen(getTagValue(element, "ref_origin"))
                    .observaciones(observacion)
                    .importe(Double.parseDouble(getTagValue(element, "importe")))
                    .tipo(getTagValue(element, "tipo"))
                    .build();

            dtos.add(dto);
        }

        return dtos;
    }

    private static String getTagValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
}
