package com.kynsoft.report.applications.query.tomaprestamo.respaldoCultural.solicitudDisposicion.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDisposicionPrestamoDto {
    
    // Fecha del documento (en la cabecera)
    private LocalDate fechaDocumento;
    
    // A. PARA USO DEL SOLICITANTE
    
    // Datos básicos del cliente
    private String nombreCliente;
    private String sucursal;
    private String municipio;
    private String cuentaDestino;
    private String acuerdoContrato;
    
    // Total a transferir
    private String moneda;
    private BigDecimal importeTotal;
    
    // Conceptos de destino - Capital de trabajo
    private BigDecimal anticipoSalarioCapitalTrabajo;
    private BigDecimal seguroCapitalTrabajo;
    private BigDecimal suministrosCapitalTrabajo;
    private String otrosConceptoCapitalTrabajo;
    private BigDecimal otrosImporteCapitalTrabajo;
    
    // Conceptos de destino - Inversiones no agropecuarias
    private BigDecimal construccionMontajes;
    private BigDecimal equiposNoAgropecuarios;
    private BigDecimal otrosInversionesNoAgropecuarias;
    
    // Conceptos de destino - Inversiones agropecuarias
    private BigDecimal anticipoSalarioInversionesAgropecuarias;
    private BigDecimal seguroInversionesAgropecuarias;
    private BigDecimal suministrosInversionesAgropecuarias;
    private String otrosConceptoInversionesAgropecuarias;
    private BigDecimal otrosImporteInversionesAgropecuarias;
    
    // Fundamentación
    private String fundamentacion;
    
    // Firmas autorizadas (pueden ser múltiples)
    private List<FirmaAutorizadaDto> firmasAutorizadas;
    
    // B. PARA USO DEL BANCO
    
    // Datos del crédito
    private String cuentaCreditoAprobado;
    private String numeroContrato;
    
    // Área Comercial
    private String revisadoAreaComercial;
    private String autorizadoAreaComercial;
    private LocalDate fechaAreaComercial;
    
    // Área Operativa
    private String recibidoAreaOperativa;
    private LocalDate fechaAreaOperativa;
    
    // Información adicional del préstamo (si es necesario)
    private String tipoCredito;
    private BigDecimal montoAprobado;
    private BigDecimal tasaInteres;
    private Integer plazoMeses;
    
    // Datos del cliente adicionales
    private String rucCliente;
    private String direccionCliente;
    private String telefonoCliente;
    
    // Información del representante legal
    private String nombreRepresentanteLegal;
    private String cargoRepresentanteLegal;
    private String dniRepresentanteLegal;
}

// DTO para las firmas autorizadas
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class FirmaAutorizadaDto {
    private String nombresApellidos;
    private String firma; // Podría ser un string o una ruta a imagen/archivo de firma
    private String cuno;
}