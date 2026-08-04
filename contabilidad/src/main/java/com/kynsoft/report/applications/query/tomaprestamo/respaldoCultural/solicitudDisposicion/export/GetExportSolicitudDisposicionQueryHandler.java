package com.kynsoft.report.applications.query.tomaprestamo.respaldoCultural.solicitudDisposicion.export;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.report.estadoCuenta.export.Response;
import com.kynsoft.report.domain.dto.PrestamoDto;
import com.kynsoft.report.domain.dto.TomaPrestamoDto;
import com.kynsoft.report.domain.services.IPrestamoService;
import com.kynsoft.report.domain.services.ITomaPrestamoService;
import java.io.InputStream;
import java.io.IOException;
import java.math.BigDecimal;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class GetExportSolicitudDisposicionQueryHandler implements IQueryHandler<GetExportSolicitudDisposicionQuery, Response> {

    private final ITomaPrestamoService tomaPrestamoService;
    private final IPrestamoService prestamoService;

    public GetExportSolicitudDisposicionQueryHandler(ITomaPrestamoService tomaPrestamoService,
                                                     IPrestamoService prestamoService) {
        this.tomaPrestamoService = tomaPrestamoService;
        this.prestamoService = prestamoService;
    }

    @Override
    public Response handle(GetExportSolicitudDisposicionQuery query) {
        // Obtener la toma de préstamo por ID
        TomaPrestamoDto tomaPrestamo = null;
        try {
            tomaPrestamo = tomaPrestamoService.findById(query.getTomaPrestamoId());
        } catch (Exception e) {
        }

        // Si la toma de préstamo no existe, se inicializa con un DTO vacío.
        if (tomaPrestamo == null) {
            tomaPrestamo = new TomaPrestamoDto();
        }

        // Obtener el préstamo asociado
        PrestamoDto prestamo = new PrestamoDto(); // Inicializamos un DTO de préstamo vacío por defecto
        String creditoId = null;
        try {
            creditoId = tomaPrestamo.getCreditoId();
        } catch (Exception e) {
        }

        // Solo intentar buscar el préstamo si el ID de crédito no es nulo ni vacío
        if (creditoId != null && !creditoId.trim().isEmpty()) {
            try {
                PrestamoDto foundPrestamo = prestamoService.findById(UUID.fromString(creditoId));
                if (foundPrestamo != null) {
                    prestamo = foundPrestamo;
                }
            } catch (IllegalArgumentException e) {
                // Si el ID no es un UUID válido, se ignora y se usa el PrestamoDto vacío inicializado.
            }
        }

        // Construir el DTO de solicitud con los datos disponibles (vacíos o reales)
        SolicitudDisposicionPrestamoDto solicitudDto = construirSolicitudDto(tomaPrestamo, prestamo);

        return Response.builder().outputStream(this.response(solicitudDto)).build();
    }

    private StreamingResponseBody response(SolicitudDisposicionPrestamoDto dto) {
        return outputStream -> {
            try (XWPFDocument document = new XWPFDocument()) {
                crearDocumento(document, dto);
                document.write(outputStream);
            } catch (Exception e) {
                throw new RuntimeException("Error al generar el documento Word", e);
            }
        };
    }

    private void crearDocumento(XWPFDocument document, SolicitudDisposicionPrestamoDto dto) {

        // Párrafo con logo y título en la misma línea
        XWPFParagraph headerParagraph = document.createParagraph();
        headerParagraph.setAlignment(ParagraphAlignment.LEFT);

        // Insertar logo
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("logo-bandec.png")) {
            if (is != null) {
                XWPFRun logoRun = headerParagraph.createRun();
                logoRun.addPicture(
                    is,
                    XWPFDocument.PICTURE_TYPE_PNG,
                    "logo-bandec.png",
                    1879600,  // Tamaño del ejemplo
                    965200
                );
            }
        } catch (InvalidFormatException | IOException e) {
            System.err.println("Error al insertar el logo: " + e.getMessage());
        }

        // Título en la misma línea
        XWPFRun titleRun = headerParagraph.createRun();
        titleRun.setText("SOLICITUD DE DISPOSICIÓN DE PRÉSTAMOS");
        titleRun.setFontFamily("Franklin Gothic Book");
        titleRun.setFontSize(12);

        // Modelo y fecha
        XWPFParagraph modelDate = document.createParagraph();
        modelDate.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun modelDateRun = modelDate.createRun();
        modelDateRun.setText("MOD. 106-664 (Rev. 07/2018)                                                 FECHA: _");
        modelDateRun.setFontFamily("Franklin Gothic Book");
        modelDateRun.setFontSize(12);
        XWPFRun dateLine = modelDate.createRun();
        dateLine.setText("__________");
        dateLine.setFontFamily("Franklin Gothic Book");
        dateLine.setFontSize(12);
        dateLine.setUnderline(UnderlinePatterns.SINGLE);

        // Espacio
        document.createParagraph();

        // A. PARA USO DEL SOLICITANTE
        XWPFParagraph sectionA = document.createParagraph();
        XWPFRun sectionARun = sectionA.createRun();
        sectionARun.setText("A. PARA USO DEL SOLICITANTE:");
        sectionARun.setBold(true);
        sectionARun.setFontSize(12);
        sectionARun.setFontFamily("Franklin Gothic Book");

        // Tabla 1: Datos básicos (3 columnas)
        XWPFTable table1 = document.createTable(2, 3);
        setTableWidth(table1, "100%");

        // Primera fila (encabezados)
        setCellText(table1.getRow(0).getCell(0), "a) NOMBRE DEL CLIENTE", false, ParagraphAlignment.LEFT, 12);
        setCellText(table1.getRow(0).getCell(1), "b)  SUCURSAL", false, ParagraphAlignment.LEFT, 12);
        setCellText(table1.getRow(0).getCell(2), "c) MUNICIPIO", false, ParagraphAlignment.LEFT, 12);

        // Segunda fila (valores) - en negrita según el ejemplo
        setCellText(table1.getRow(1).getCell(0), dto.getNombreCliente() != null ? dto.getNombreCliente() : "UBP Lorenzo Daliz", true, ParagraphAlignment.LEFT, 12);
        setCellText(table1.getRow(1).getCell(1), dto.getSucursal() != null ? dto.getSucursal() : "6241", true, ParagraphAlignment.LEFT, 12);
        setCellText(table1.getRow(1).getCell(2), dto.getMunicipio() != null ? dto.getMunicipio() : "Manatí", true, ParagraphAlignment.LEFT, 12);

        // Espacio
        document.createParagraph();

        // Tabla 2: Estructura compleja con celdas fusionadas (3 filas)
        XWPFTable table2 = document.createTable(3, 3);
        setTableWidth(table2, "100%");

        // Primera fila (encabezados principales)
        setCellText(table2.getRow(0).getCell(0), "d) CUENTA DE DESTINO", false, ParagraphAlignment.CENTER, 12);
        setCellText(table2.getRow(0).getCell(1), "e) ACUERDO O\nCONTRATO", false, ParagraphAlignment.CENTER, 12);
        setCellText(table2.getRow(0).getCell(2), "f) TOTAL A TRANSFERIR", false, ParagraphAlignment.CENTER, 12);

        // Segunda fila (sub-encabezados para total)
        setCellText(table2.getRow(1).getCell(0), "", false, ParagraphAlignment.LEFT, 12);
        setCellText(table2.getRow(1).getCell(1), "", false, ParagraphAlignment.LEFT, 12);

        // Sub-encabezados Moneda e Importe
        XWPFTableCell monedaCell = table2.getRow(1).getCell(2);
        monedaCell.removeParagraph(0);
        XWPFParagraph monedaPara = monedaCell.addParagraph();
        monedaPara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun monedaRun = monedaPara.createRun();
        monedaRun.setText("Moneda");
        monedaRun.setFontFamily("Franklin Gothic Book");
        monedaRun.setFontSize(12);

        XWPFParagraph importePara = monedaCell.addParagraph();
        importePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun importeRun = importePara.createRun();
        importeRun.setText("Importe");
        importeRun.setFontFamily("Franklin Gothic Book");
        importeRun.setFontSize(12);

        // Tercera fila (valores)
        setCellText(table2.getRow(2).getCell(0), dto.getCuentaDestino() != null ? dto.getCuentaDestino() : "", false, ParagraphAlignment.LEFT, 12);
        setCellText(table2.getRow(2).getCell(1), dto.getAcuerdoContrato() != null ? dto.getAcuerdoContrato() : "", false, ParagraphAlignment.CENTER, 12);

        // Valores de Moneda e Importe
        XWPFTableCell valoresCell = table2.getRow(2).getCell(2);
        valoresCell.removeParagraph(0);
        XWPFParagraph monedaValPara = valoresCell.addParagraph();
        monedaValPara.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun monedaValRun = monedaValPara.createRun();
        monedaValRun.setText(dto.getMoneda() != null ? dto.getMoneda() : "CUP");
        monedaValRun.setFontFamily("Franklin Gothic Book");
        monedaValRun.setFontSize(12);

        XWPFParagraph importeValPara = valoresCell.addParagraph();
        importeValPara.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun importeValRun = importeValPara.createRun();
        importeValRun.setText(dto.getImporteTotal() != null ? dto.getImporteTotal().toString() : "");
        importeValRun.setFontFamily("Franklin Gothic Book");
        importeValRun.setFontSize(12);

        // Tabla grande para conceptos de destino (dentro de una celda única)
        XWPFTable conceptosTable = document.createTable(1, 1);
        setTableWidth(conceptosTable, "100%");
        XWPFTableCell conceptosCell = conceptosTable.getRow(0).getCell(0);
        conceptosCell.removeParagraph(0);

        // g) CONCEPTOS DE DESTINO
        XWPFParagraph conceptosTitle = conceptosCell.addParagraph();
        XWPFRun conceptosRun = conceptosTitle.createRun();
        conceptosRun.setText("g) CONCEPTOS DE DESTINO");
        conceptosRun.setFontFamily("Franklin Gothic Book");
        conceptosRun.setFontSize(10);

        // Espacio
        conceptosCell.addParagraph();

        // 1) Capital de trabajo
        XWPFParagraph capitalTrabajoTitle = conceptosCell.addParagraph();
        XWPFRun capitalRun = capitalTrabajoTitle.createRun();
        capitalRun.setText("1) Capital de trabajo (Importe de cada destino)");
        capitalRun.setFontFamily("Franklin Gothic Book");
        capitalRun.setFontSize(10);

        // Detalles de Capital de trabajo
        XWPFParagraph capitalDetails = conceptosCell.addParagraph();
        XWPFRun capitalDetailsRun = capitalDetails.createRun();
        capitalDetailsRun.setFontFamily("Franklin Gothic Book");
        capitalDetailsRun.setFontSize(10);
        capitalDetailsRun.setText("001-Antic. y salario ");

        XWPFRun capitalVal1 = capitalDetails.createRun();
        capitalVal1.setFontFamily("Franklin Gothic Book");
        capitalVal1.setFontSize(10);
        capitalVal1.setBold(true);
        capitalVal1.setUnderline(UnderlinePatterns.SINGLE);
        capitalVal1.setText("_________________");

        XWPFRun capitalRun2 = capitalDetails.createRun();
        capitalRun2.setFontFamily("Franklin Gothic Book");
        capitalRun2.setFontSize(10);
        capitalRun2.setText(" 002-Seguro:_________003 Suministros ");

        XWPFRun capitalVal2 = capitalDetails.createRun();
        capitalVal2.setFontFamily("Franklin Gothic Book");
        capitalVal2.setFontSize(12);
        capitalVal2.setBold(true);
        capitalVal2.setUnderline(UnderlinePatterns.SINGLE);
        capitalVal2.setText("____________");

        // Otros de Capital de trabajo
        XWPFParagraph otrosCapital = conceptosCell.addParagraph();
        XWPFRun otrosCapitalRun = otrosCapital.createRun();
        otrosCapitalRun.setFontFamily("Franklin Gothic Book");
        otrosCapitalRun.setFontSize(10);
        otrosCapitalRun.setText("Otros: Concepto: ");

        XWPFRun otrosVal1 = otrosCapital.createRun();
        otrosVal1.setFontFamily("Franklin Gothic Book");
        otrosVal1.setFontSize(10);
        otrosVal1.setBold(true);
        otrosVal1.setUnderline(UnderlinePatterns.SINGLE);
        otrosVal1.setText("_____________________");

        XWPFRun otrosRun2 = otrosCapital.createRun();
        otrosRun2.setFontFamily("Franklin Gothic Book");
        otrosRun2.setFontSize(10);
        otrosRun2.setText("Importe: ");

        XWPFRun otrosVal2 = otrosCapital.createRun();
        otrosVal2.setFontFamily("Franklin Gothic Book");
        otrosVal2.setFontSize(10);
        otrosVal2.setBold(true);
        otrosVal2.setUnderline(UnderlinePatterns.SINGLE);
        otrosVal2.setText("___________________");

        // Espacio
        conceptosCell.addParagraph();

        // 2) Inversiones no agropecuarias
        XWPFParagraph invNoAgroTitle = conceptosCell.addParagraph();
        XWPFRun invNoAgroRun = invNoAgroTitle.createRun();
        invNoAgroRun.setText("2) Inversiones no agropecuarias( Importe de cada destino)");
        invNoAgroRun.setFontFamily("Franklin Gothic Book");
        invNoAgroRun.setFontSize(10);

        // Detalles de Inversiones no agropecuarias
        XWPFParagraph invNoAgroDetails = conceptosCell.addParagraph();
        invNoAgroDetails.setIndentationLeft(180);
        XWPFRun invNoAgroDetailsRun = invNoAgroDetails.createRun();
        invNoAgroDetailsRun.setFontFamily("Franklin Gothic Book");
        invNoAgroDetailsRun.setFontSize(10);
        invNoAgroDetailsRun.setText("         1.Construcción y Montajes:_____________2.Equipos:_____________3.Otros:___________");

        // Espacio
        conceptosCell.addParagraph();

        // 3) Inversiones agropecuarias
        XWPFParagraph invAgroTitle = conceptosCell.addParagraph();
        XWPFRun invAgroRun = invAgroTitle.createRun();
        invAgroRun.setText("3) Inversiones agropecuarias (Importe de cada destino)");
        invAgroRun.setFontFamily("Franklin Gothic Book");
        invAgroRun.setFontSize(10);

        // Detalles de Inversiones agropecuarias
        XWPFParagraph invAgroDetails = conceptosCell.addParagraph();
        XWPFRun invAgroDetailsRun = invAgroDetails.createRun();
        invAgroDetailsRun.setFontFamily("Franklin Gothic Book");
        invAgroDetailsRun.setFontSize(10);
        invAgroDetailsRun.setText("001-Antic. y salario:______________ 002-Seguro:__________003 Suministros________________");

        // Otros de Inversiones agropecuarias
        XWPFParagraph otrosInvAgro = conceptosCell.addParagraph();
        XWPFRun otrosInvAgroRun = otrosInvAgro.createRun();
        otrosInvAgroRun.setFontFamily("Franklin Gothic Book");
        otrosInvAgroRun.setFontSize(10);
        otrosInvAgroRun.setUnderline(UnderlinePatterns.SINGLE);
        otrosInvAgroRun.setText("Otros: Concepto: Importe:");

        // Línea de separación
        XWPFParagraph separator = conceptosCell.addParagraph();
        XWPFRun separatorRun = separator.createRun();
        separatorRun.setFontFamily("Franklin Gothic Book");
        separatorRun.setFontSize(12);
        separatorRun.setText("____________________________________________________________________________");


        // h) FUNDAMENTACIÓN dentro de la tabla
        XWPFParagraph fundamentacionTitle = conceptosCell.addParagraph();
        XWPFRun fundamentacionTitleRun = fundamentacionTitle.createRun();
        fundamentacionTitleRun.setText("h) FUNDAMENTACIÓN");
        fundamentacionTitleRun.setFontFamily("Franklin Gothic Book");
        fundamentacionTitleRun.setFontSize(12);

        // Líneas para fundamentación con subrayado
        XWPFParagraph fundamentacion1 = conceptosCell.addParagraph();
        XWPFRun fundamentacionRun1 = fundamentacion1.createRun();
        fundamentacionRun1.setFontFamily("Franklin Gothic Book");
        fundamentacionRun1.setFontSize(9);
        fundamentacionRun1.setUnderline(UnderlinePatterns.SINGLE);
        fundamentacionRun1.setText(dto.getFundamentacion() != null && !dto.getFundamentacion().isEmpty()
                ? dto.getFundamentacion()
                : "                                                                                                                                                ");

        XWPFParagraph fundamentacion2 = conceptosCell.addParagraph();
        XWPFRun fundamentacionRun2 = fundamentacion2.createRun();
        fundamentacionRun2.setFontFamily("Franklin Gothic Book");
        fundamentacionRun2.setFontSize(9);
        fundamentacionRun2.setUnderline(UnderlinePatterns.SINGLE);
        fundamentacionRun2.setText("                                                                                                                                                ");
        
        // Espacio
        document.createParagraph();

        // i) FIRMAS AUTORIZADAS
        XWPFParagraph firmasTitle = document.createParagraph();
        XWPFRun firmasTitleRun = firmasTitle.createRun();
        firmasTitleRun.setText("i) FIRMAS AUTORIZADAS");
        firmasTitleRun.setBold(true);
        firmasTitleRun.setFontFamily("Franklin Gothic Book");
        firmasTitleRun.setFontSize(12);

        // Tabla para firmas (4 filas, 3 columnas)
        XWPFTable firmasTable = document.createTable(4, 3);
        setTableWidth(firmasTable, "100%");

        // Encabezados
        setCellText(firmasTable.getRow(0).getCell(0), "Nombres y Apellidos", true, ParagraphAlignment.LEFT, 12);
        setCellText(firmasTable.getRow(0).getCell(1), "Firmas", true, ParagraphAlignment.LEFT, 12);
        setCellText(firmasTable.getRow(0).getCell(2), "Cuño", true, ParagraphAlignment.LEFT, 12);

        // Llenar firmas (hasta 3, las filas 1, 2, 3)
        for (int i = 0; i < 3; i++) {
            if (dto.getFirmasAutorizadas() != null && i < dto.getFirmasAutorizadas().size()) {
                var firma = dto.getFirmasAutorizadas().get(i);
                setCellText(firmasTable.getRow(i + 1).getCell(0), firma.getNombresApellidos() != null ? firma.getNombresApellidos() : " ", false, ParagraphAlignment.LEFT, 12);
                setCellText(firmasTable.getRow(i + 1).getCell(1), firma.getFirma() != null ? firma.getFirma() : " ", false, ParagraphAlignment.LEFT, 12);
                setCellText(firmasTable.getRow(i + 1).getCell(2), firma.getCuno() != null ? firma.getCuno() : " ", false, ParagraphAlignment.LEFT, 12);
            } else {
                // Rellenar con espacio si no hay más firmas para mantener el formato
                setCellText(firmasTable.getRow(i + 1).getCell(0), " ", false, ParagraphAlignment.LEFT, 12);
                setCellText(firmasTable.getRow(i + 1).getCell(1), " ", false, ParagraphAlignment.LEFT, 12);
                setCellText(firmasTable.getRow(i + 1).getCell(2), " ", false, ParagraphAlignment.LEFT, 12);
            }
        }
        
        // Espacio
        document.createParagraph();
        
        // B. PARA USO DEL BANCO
        XWPFParagraph sectionB = document.createParagraph();
        XWPFRun sectionBRun = sectionB.createRun();
        sectionBRun.setText("B. PARA USO DEL BANCO");
        sectionBRun.setBold(true);
        sectionBRun.setFontSize(12);
        sectionBRun.setFontFamily("Franklin Gothic Book");

        // j) CUENTA DEL CRÉDITO APROBADO
        XWPFParagraph cuentaCredito = document.createParagraph();
        XWPFRun cuentaCreditoRun = cuentaCredito.createRun();
        cuentaCreditoRun.setFontFamily("Franklin Gothic Book");
        cuentaCreditoRun.setFontSize(12);
        cuentaCreditoRun.setText("j) CUENTA DEL CRÉDITO APROBADO: "
                + (dto.getCuentaCreditoAprobado() != null ? dto.getCuentaCreditoAprobado() : "________________"));

        // k) NOMBRES, APELLIDOS Y FIRMAS AUTORIZADAS
        XWPFParagraph firmasBancoTitle = document.createParagraph();
        XWPFRun firmasBancoRun = firmasBancoTitle.createRun();
        firmasBancoRun.setText("k) NOMBRES, APELLIDOS Y FIRMAS AUTORIZADAS:");
        firmasBancoRun.setBold(true);
        firmasBancoRun.setFontFamily("Franklin Gothic Book");
        firmasBancoRun.setFontSize(12);

        // Tabla para áreas comercial y operativa (3 filas, 5 columnas) 
        XWPFTable bancoTable = document.createTable(3, 5);
        setTableWidth(bancoTable, "100%");
        
        // Ajuste de ancho de columnas para simular el documento (ancho proporcional)
        setCellWidth(bancoTable.getRow(0).getCell(0), "20%");
        setCellWidth(bancoTable.getRow(0).getCell(1), "20%");
        setCellWidth(bancoTable.getRow(0).getCell(2), "10%");
        setCellWidth(bancoTable.getRow(0).getCell(3), "30%"); // Área Operativa toma 2 celdas
        setCellWidth(bancoTable.getRow(0).getCell(4), "20%"); // La última celda de Área Operativa

        // Fusión de celdas para encabezados de áreas (fila 0)
        mergeCellsHorizontal(bancoTable.getRow(0), 0, 2); // Área Comercial (0-2)
        mergeCellsHorizontal(bancoTable.getRow(0), 3, 4); // Área Operativa (3-4)

        // Primera fila con áreas
        setCellText(bancoTable.getRow(0).getCell(0), "AREA COMERCIAL", true, ParagraphAlignment.CENTER, 12);
        setCellText(bancoTable.getRow(0).getCell(3), "AREA OPERATIVA", true, ParagraphAlignment.CENTER, 12);

        // Segunda fila con subtítulos
        setCellText(bancoTable.getRow(1).getCell(0), "Revisado", true, ParagraphAlignment.CENTER, 12);
        setCellText(bancoTable.getRow(1).getCell(1), "Autorizado", true, ParagraphAlignment.CENTER, 12);
        setCellText(bancoTable.getRow(1).getCell(2), "Fecha", true, ParagraphAlignment.CENTER, 12);
        setCellText(bancoTable.getRow(1).getCell(3), "Recibido", true, ParagraphAlignment.CENTER, 12);
        setCellText(bancoTable.getRow(1).getCell(4), "Fecha", true, ParagraphAlignment.CENTER, 12);

        // Tercera fila con D M A y valores

        // Encabezado de fecha (D M A) para Area Comercial
        XWPFTableCell fechaComercialHeaderCell = bancoTable.getRow(2).getCell(2);
        fechaComercialHeaderCell.removeParagraph(0);
        XWPFParagraph fechaComercialHeaderPara = fechaComercialHeaderCell.addParagraph();
        fechaComercialHeaderPara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun fechaComercialHeaderRun = fechaComercialHeaderPara.createRun();
        fechaComercialHeaderRun.setFontFamily("Franklin Gothic Book");
        fechaComercialHeaderRun.setFontSize(12);
        fechaComercialHeaderRun.setText("D\tM\tA");

        // Encabezado de fecha (D M A) para Area Operativa
        XWPFTableCell fechaOperativaHeaderCell = bancoTable.getRow(2).getCell(4);
        fechaOperativaHeaderCell.removeParagraph(0);
        XWPFParagraph fechaOperativaHeaderPara = fechaOperativaHeaderCell.addParagraph();
        fechaOperativaHeaderPara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun fechaOperativaHeaderRun = fechaOperativaHeaderPara.createRun();
        fechaOperativaHeaderRun.setFontFamily("Franklin Gothic Book");
        fechaOperativaHeaderRun.setFontSize(12);
        fechaOperativaHeaderRun.setText("D\tM\tA");

        // Valores de Area Comercial (Revisado y Autorizado)
        setCellText(bancoTable.getRow(2).getCell(0), dto.getRevisadoAreaComercial() != null ? dto.getRevisadoAreaComercial() : " ", false, ParagraphAlignment.CENTER, 12);
        setCellText(bancoTable.getRow(2).getCell(1), dto.getAutorizadoAreaComercial() != null ? dto.getAutorizadoAreaComercial() : " ", false, ParagraphAlignment.CENTER, 12);

        // Celda para fecha comercial (valores D M A)
        if (dto.getFechaAreaComercial() != null) {
            XWPFTableCell fechaComercialValCell = bancoTable.getRow(2).getCell(2);
            fechaComercialValCell.removeParagraph(0); // Remover el D M A
            XWPFParagraph fechaComercialValPara = fechaComercialValCell.addParagraph();
            fechaComercialValPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun fechaComercialValRun = fechaComercialValPara.createRun();
            fechaComercialValRun.setFontFamily("Franklin Gothic Book");
            fechaComercialValRun.setFontSize(12);
            fechaComercialValRun.setText(
                    dto.getFechaAreaComercial().getDayOfMonth() + "\t"
                    + dto.getFechaAreaComercial().getMonthValue() + "\t"
                    + dto.getFechaAreaComercial().getYear()
            );
        }

        // Valores de Area Operativa (Recibido)
        setCellText(bancoTable.getRow(2).getCell(3), dto.getRecibidoAreaOperativa() != null ? dto.getRecibidoAreaOperativa() : " ", false, ParagraphAlignment.CENTER, 12);

        // Celda para fecha operativa (valores D M A)
        if (dto.getFechaAreaOperativa() != null) {
            XWPFTableCell fechaOperativaValCell = bancoTable.getRow(2).getCell(4);
            fechaOperativaValCell.removeParagraph(0); // Remover el D M A
            XWPFParagraph fechaOperativaValPara = fechaOperativaValCell.addParagraph();
            fechaOperativaValPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun fechaOperativaValRun = fechaOperativaValPara.createRun();
            fechaOperativaValRun.setFontFamily("Franklin Gothic Book");
            fechaOperativaValRun.setFontSize(12);
            fechaOperativaValRun.setText(
                    dto.getFechaAreaOperativa().getDayOfMonth() + "\t"
                    + dto.getFechaAreaOperativa().getMonthValue() + "\t"
                    + dto.getFechaAreaOperativa().getYear()
            );
        }
    }

    // Método auxiliar modificado para mejor control de celdas
    private void setCellText(XWPFTableCell cell, String text, boolean bold, ParagraphAlignment alignment, int fontSize) {
        cell.removeParagraph(0);
        XWPFParagraph paragraph = cell.addParagraph();
        paragraph.setAlignment(alignment);
        XWPFRun run = paragraph.createRun();
        run.setText(text != null ? text : "");
        run.setBold(bold);
        run.setFontSize(fontSize);
        run.setFontFamily("Franklin Gothic Book");
    }

    private void setCellText(XWPFTableCell cell, String text) {
        setCellText(cell, text, false, ParagraphAlignment.LEFT, 10);
    }
    
    // Método para establecer el ancho de la tabla al 100%
    private void setTableWidth(XWPFTable table, String width) {
        CTTblWidth tblWidth = table.getCTTbl().addNewTblPr().addNewTblW();
        tblWidth.setType(STTblWidth.DXA);
        tblWidth.setW(java.math.BigInteger.valueOf(9072)); // 9072 es el valor para el 100%
    }
    
    // Método para establecer el ancho de una celda
    private void setCellWidth(XWPFTableCell cell, String widthPercentage) {
        // Simple implementación que asume un ancho total de 9072 (100%)
        int percentage = Integer.parseInt(widthPercentage.replace("%", ""));
        int widthDxa = (int) (9072 * (percentage / 100.0));

        CTTblWidth tblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
        tblWidth.setType(STTblWidth.DXA);
        tblWidth.setW(java.math.BigInteger.valueOf(widthDxa));
    }
    
    // Método para fusionar celdas horizontalmente
    private void mergeCellsHorizontal(XWPFTableRow row, int fromCell, int toCell) {
        for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {
            XWPFTableCell cell = row.getCell(cellIndex);
            if ( cellIndex == fromCell ) {
                // The first cell in the merge group is set to restart the merge
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge.RESTART);
            } else {
                // The subsequent cells are set to continue the merge
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge.CONTINUE);
            }
        }
    }

    private SolicitudDisposicionPrestamoDto construirSolicitudDto(TomaPrestamoDto tomaPrestamo, PrestamoDto prestamo) {
        // Mapeo de datos siguiendo el formato del ejemplo

        return SolicitudDisposicionPrestamoDto.builder()
                .fechaDocumento(tomaPrestamo.getFecha())
                .nombreCliente("UBP Lorenzo Daliz")
                .sucursal("6241")
                .municipio("Manatí")
                .cuentaDestino(tomaPrestamo.getCuentaDestino())
                .acuerdoContrato(prestamo.getNumeroContrato())
                .moneda("CUP")
                .importeTotal(tomaPrestamo.getImporte() != null
                        ? BigDecimal.valueOf(tomaPrestamo.getImporte()) : BigDecimal.ZERO)
                .cuentaCreditoAprobado(prestamo.getCuenta())
                .numeroContrato(prestamo.getNumeroContrato())
                .fundamentacion(tomaPrestamo.getObservaciones())
                .firmasAutorizadas(Collections.emptyList())
                .build();
    }
    
    // --- CLASES DTO ASUMIDAS (NO DEBEN ESTAR EN EL HANDLER REAL, SÓLO PARA CONTEXTO) ---
    // NOTA: Estas clases deben estar definidas en sus paquetes correspondientes,
    // se incluyen aquí para hacer el código de referencia completo.

    private static class SolicitudDisposicionPrestamoDto {
        private final java.time.LocalDate fechaDocumento;
        private final String nombreCliente;
        private final String sucursal;
        private final String municipio;
        private final String cuentaDestino;
        private final String acuerdoContrato;
        private final String moneda;
        private final BigDecimal importeTotal;
        private final BigDecimal anticipoSalarioCapitalTrabajo;
        private final BigDecimal seguroCapitalTrabajo;
        private final BigDecimal suministrosCapitalTrabajo;
        private final String otrosConceptoCapitalTrabajo;
        private final BigDecimal otrosImporteCapitalTrabajo;
        private final BigDecimal construccionMontajes;
        private final BigDecimal equiposNoAgropecuarios;
        private final BigDecimal otrosInversionesNoAgropecuarias;
        private final BigDecimal anticipoSalarioInversionesAgropecuarias;
        private final BigDecimal seguroInversionesAgropecuarias;
        private final BigDecimal suministrosInversionesAgropecuarias;
        private final String otrosConceptoInversionesAgropecuarias;
        private final BigDecimal otrosImporteInversionesAgropecuarias;
        private final String fundamentacion;
        private final List<FirmasAutorizadasDto> firmasAutorizadas;
        private final String cuentaCreditoAprobado;
        private final String numeroContrato;
        private final String revisadoAreaComercial;
        private final String autorizadoAreaComercial;
        private final java.time.LocalDate fechaAreaComercial;
        private final String recibidoAreaOperativa;
        private final java.time.LocalDate fechaAreaOperativa;

        // Constructor privado para Builder
        private SolicitudDisposicionPrestamoDto(Builder builder) {
            this.fechaDocumento = builder.fechaDocumento;
            this.nombreCliente = builder.nombreCliente;
            this.sucursal = builder.sucursal;
            this.municipio = builder.municipio;
            this.cuentaDestino = builder.cuentaDestino;
            this.acuerdoContrato = builder.acuerdoContrato;
            this.moneda = builder.moneda;
            this.importeTotal = builder.importeTotal;
            this.anticipoSalarioCapitalTrabajo = builder.anticipoSalarioCapitalTrabajo;
            this.seguroCapitalTrabajo = builder.seguroCapitalTrabajo;
            this.suministrosCapitalTrabajo = builder.suministrosCapitalTrabajo;
            this.otrosConceptoCapitalTrabajo = builder.otrosConceptoCapitalTrabajo;
            this.otrosImporteCapitalTrabajo = builder.otrosImporteCapitalTrabajo;
            this.construccionMontajes = builder.construccionMontajes;
            this.equiposNoAgropecuarios = builder.equiposNoAgropecuarios;
            this.otrosInversionesNoAgropecuarias = builder.otrosInversionesNoAgropecuarias;
            this.anticipoSalarioInversionesAgropecuarias = builder.anticipoSalarioInversionesAgropecuarias;
            this.seguroInversionesAgropecuarias = builder.seguroInversionesAgropecuarias;
            this.suministrosInversionesAgropecuarias = builder.suministrosInversionesAgropecuarias;
            this.otrosConceptoInversionesAgropecuarias = builder.otrosConceptoInversionesAgropecuarias;
            this.otrosImporteInversionesAgropecuarias = builder.otrosImporteInversionesAgropecuarias;
            this.fundamentacion = builder.fundamentacion;
            this.firmasAutorizadas = builder.firmasAutorizadas;
            this.cuentaCreditoAprobado = builder.cuentaCreditoAprobado;
            this.numeroContrato = builder.numeroContrato;
            this.revisadoAreaComercial = builder.revisadoAreaComercial;
            this.autorizadoAreaComercial = builder.autorizadoAreaComercial;
            this.fechaAreaComercial = builder.fechaAreaComercial;
            this.recibidoAreaOperativa = builder.recibidoAreaOperativa;
            this.fechaAreaOperativa = builder.fechaAreaOperativa;
        }

        public static Builder builder() {
            return new Builder();
        }

        // Getters
        public java.time.LocalDate getFechaDocumento() { return fechaDocumento; }
        public String getNombreCliente() { return nombreCliente; }
        public String getSucursal() { return sucursal; }
        public String getMunicipio() { return municipio; }
        public String getCuentaDestino() { return cuentaDestino; }
        public String getAcuerdoContrato() { return acuerdoContrato; }
        public String getMoneda() { return moneda; }
        public BigDecimal getImporteTotal() { return importeTotal; }
        public BigDecimal getAnticipoSalarioCapitalTrabajo() { return anticipoSalarioCapitalTrabajo; }
        public BigDecimal getSeguroCapitalTrabajo() { return seguroCapitalTrabajo; }
        public BigDecimal getSuministrosCapitalTrabajo() { return suministrosCapitalTrabajo; }
        public String getOtrosConceptoCapitalTrabajo() { return otrosConceptoCapitalTrabajo; }
        public BigDecimal getOtrosImporteCapitalTrabajo() { return otrosImporteCapitalTrabajo; }
        public BigDecimal getConstruccionMontajes() { return construccionMontajes; }
        public BigDecimal getEquiposNoAgropecuarios() { return equiposNoAgropecuarios; }
        public BigDecimal getOtrosInversionesNoAgropecuarias() { return otrosInversionesNoAgropecuarias; }
        public BigDecimal getAnticipoSalarioInversionesAgropecuarias() { return anticipoSalarioInversionesAgropecuarias; }
        public BigDecimal getSeguroInversionesAgropecuarias() { return seguroInversionesAgropecuarias; }
        public BigDecimal getSuministrosInversionesAgropecuarias() { return suministrosInversionesAgropecuarias; }
        public String getOtrosConceptoInversionesAgropecuarias() { return otrosConceptoInversionesAgropecuarias; }
        public BigDecimal getOtrosImporteInversionesAgropecuarias() { return otrosImporteInversionesAgropecuarias; }
        public String getFundamentacion() { return fundamentacion; }
        public List<FirmasAutorizadasDto> getFirmasAutorizadas() { return firmasAutorizadas; }
        public String getCuentaCreditoAprobado() { return cuentaCreditoAprobado; }
        public String getNumeroContrato() { return numeroContrato; }
        public String getRevisadoAreaComercial() { return revisadoAreaComercial; }
        public String getAutorizadoAreaComercial() { return autorizadoAreaComercial; }
        public java.time.LocalDate getFechaAreaComercial() { return fechaAreaComercial; }
        public String getRecibidoAreaOperativa() { return recibidoAreaOperativa; }
        public java.time.LocalDate getFechaAreaOperativa() { return fechaAreaOperativa; }

        public static class Builder {
            private java.time.LocalDate fechaDocumento;
            private String nombreCliente;
            private String sucursal;
            private String municipio;
            private String cuentaDestino;
            private String acuerdoContrato;
            private String moneda;
            private BigDecimal importeTotal;
            private BigDecimal anticipoSalarioCapitalTrabajo;
            private BigDecimal seguroCapitalTrabajo;
            private BigDecimal suministrosCapitalTrabajo;
            private String otrosConceptoCapitalTrabajo;
            private BigDecimal otrosImporteCapitalTrabajo;
            private BigDecimal construccionMontajes;
            private BigDecimal equiposNoAgropecuarios;
            private BigDecimal otrosInversionesNoAgropecuarias;
            private BigDecimal anticipoSalarioInversionesAgropecuarias;
            private BigDecimal seguroInversionesAgropecuarias;
            private BigDecimal suministrosInversionesAgropecuarias;
            private String otrosConceptoInversionesAgropecuarias;
            private BigDecimal otrosImporteInversionesAgropecuarias;
            private String fundamentacion;
            private List<FirmasAutorizadasDto> firmasAutorizadas;
            private String cuentaCreditoAprobado;
            private String numeroContrato;
            private String revisadoAreaComercial;
            private String autorizadoAreaComercial;
            private java.time.LocalDate fechaAreaComercial;
            private String recibidoAreaOperativa;
            private java.time.LocalDate fechaAreaOperativa;

            public Builder fechaDocumento(java.time.LocalDate fechaDocumento) { this.fechaDocumento = fechaDocumento; return this; }
            public Builder nombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; return this; }
            public Builder sucursal(String sucursal) { this.sucursal = sucursal; return this; }
            public Builder municipio(String municipio) { this.municipio = municipio; return this; }
            public Builder cuentaDestino(String cuentaDestino) { this.cuentaDestino = cuentaDestino; return this; }
            public Builder acuerdoContrato(String acuerdoContrato) { this.acuerdoContrato = acuerdoContrato; return this; }
            public Builder moneda(String moneda) { this.moneda = moneda; return this; }
            public Builder importeTotal(BigDecimal importeTotal) { this.importeTotal = importeTotal; return this; }
            public Builder anticipoSalarioCapitalTrabajo(BigDecimal anticipoSalarioCapitalTrabajo) { this.anticipoSalarioCapitalTrabajo = anticipoSalarioCapitalTrabajo; return this; }
            public Builder seguroCapitalTrabajo(BigDecimal seguroCapitalTrabajo) { this.seguroCapitalTrabajo = seguroCapitalTrabajo; return this; }
            public Builder suministrosCapitalTrabajo(BigDecimal suministrosCapitalTrabajo) { this.suministrosCapitalTrabajo = suministrosCapitalTrabajo; return this; }
            public Builder otrosConceptoCapitalTrabajo(String otrosConceptoCapitalTrabajo) { this.otrosConceptoCapitalTrabajo = otrosConceptoCapitalTrabajo; return this; }
            public Builder otrosImporteCapitalTrabajo(BigDecimal otrosImporteCapitalTrabajo) { this.otrosImporteCapitalTrabajo = otrosImporteCapitalTrabajo; return this; }
            public Builder construccionMontajes(BigDecimal construccionMontajes) { this.construccionMontajes = construccionMontajes; return this; }
            public Builder equiposNoAgropecuarios(BigDecimal equiposNoAgropecuarios) { this.equiposNoAgropecuarios = equiposNoAgropecuarios; return this; }
            public Builder otrosInversionesNoAgropecuarias(BigDecimal otrosInversionesNoAgropecuarias) { this.otrosInversionesNoAgropecuarias = otrosInversionesNoAgropecuarias; return this; }
            public Builder anticipoSalarioInversionesAgropecuarias(BigDecimal anticipoSalarioInversionesAgropecuarias) { this.anticipoSalarioInversionesAgropecuarias = anticipoSalarioInversionesAgropecuarias; return this; }
            public Builder seguroInversionesAgropecuarias(BigDecimal seguroInversionesAgropecuarias) { this.seguroInversionesAgropecuarias = seguroInversionesAgropecuarias; return this; }
            public Builder suministrosInversionesAgropecuarias(BigDecimal suministrosInversionesAgropecuarias) { this.suministrosInversionesAgropecuarias = suministrosInversionesAgropecuarias; return this; }
            public Builder otrosConceptoInversionesAgropecuarias(String otrosConceptoInversionesAgropecuarias) { this.otrosConceptoInversionesAgropecuarias = otrosConceptoInversionesAgropecuarias; return this; }
            public Builder otrosImporteInversionesAgropecuarias(BigDecimal otrosImporteInversionesAgropecuarias) { this.otrosImporteInversionesAgropecuarias = otrosImporteInversionesAgropecuarias; return this; }
            public Builder fundamentacion(String fundamentacion) { this.fundamentacion = fundamentacion; return this; }
            public Builder firmasAutorizadas(List<FirmasAutorizadasDto> firmasAutorizadas) { this.firmasAutorizadas = firmasAutorizadas; return this; }
            public Builder cuentaCreditoAprobado(String cuentaCreditoAprobado) { this.cuentaCreditoAprobado = cuentaCreditoAprobado; return this; }
            public Builder numeroContrato(String numeroContrato) { this.numeroContrato = numeroContrato; return this; }
            public Builder revisadoAreaComercial(String revisadoAreaComercial) { this.revisadoAreaComercial = revisadoAreaComercial; return this; }
            public Builder autorizadoAreaComercial(String autorizadoAreaComercial) { this.autorizadoAreaComercial = autorizadoAreaComercial; return this; }
            public Builder fechaAreaComercial(java.time.LocalDate fechaAreaComercial) { this.fechaAreaComercial = fechaAreaComercial; return this; }
            public Builder recibidoAreaOperativa(String recibidoAreaOperativa) { this.recibidoAreaOperativa = recibidoAreaOperativa; return this; }
            public Builder fechaAreaOperativa(java.time.LocalDate fechaAreaOperativa) { this.fechaAreaOperativa = fechaAreaOperativa; return this; }

            public SolicitudDisposicionPrestamoDto build() {
                return new SolicitudDisposicionPrestamoDto(this);
            }
        }
    }

    private static class FirmasAutorizadasDto {
        private final String nombresApellidos;
        private final String firma;
        private final String cuno;
        
        // Constructor para ejemplo
        public FirmasAutorizadasDto(String nombresApellidos, String firma, String cuno) {
            this.nombresApellidos = nombresApellidos;
            this.firma = firma;
            this.cuno = cuno;
        }

        public String getNombresApellidos() { return nombresApellidos; }
        public String getFirma() { return firma; }
        public String getCuno() { return cuno; }
    }
}