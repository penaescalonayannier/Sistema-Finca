---
document: mapa-formas-numeradas-caja-banco
version: 1.0.0
status: implementacion-progresiva
review_date: 2026-09-17
scope: [caja, banco, consecutivos, auditoria]
---

# Mapa de formas numeradas de Caja y Banco

## Regla de integración

Cada documento primario de Caja o Banco se enlaza con el registro general por
`emision_forma_numerada.documento_tipo` y
`emision_forma_numerada.documento_id`. El código es estable, en mayúsculas y
no se deriva de un texto visible ni de un UUID.

Los UUID de `movimiento_caja`, sus denominaciones y saldos son identificadores
técnicos y no consumen formas. La operación que los origine debe referenciar
el documento numerado correspondiente.

La anulación conserva la emisión y el número. Una reimpresión crea un evento
de impresión en el libro de emisiones, no una nueva forma ni un nuevo número.

## Documentos de Caja

| Código de forma | Tabla / tipo origen | Nombre operativo | Alcance inicial recomendado | Estado documental |
|---|---|---|---|---|
| `INGRESO_CAJA` | `documento_caja`, `RECIBO_EFECTIVO` | Recibo de efectivo | Finca o caja responsable | `ACTIVO` / `ANULADO` |
| `VALE_PAGO_MENOR` | `documento_caja`, `VALE_PAGO_MENOR` | Vale de pago menor | Finca o caja responsable | `ACTIVO` / `ANULADO` |
| `ANTICIPO_CAJA` | `documento_caja`, `ANTICIPO` | Anticipo | Finca o caja responsable | `ACTIVO` / `ANULADO` |
| `LIQUIDACION_ANTICIPO` | `documento_caja`, `LIQUIDACION_ANTICIPO` | Liquidación de anticipo | Finca o caja responsable | `ACTIVO` / `ANULADO` |
| `REEMBOLSO_CAJA` | `documento_caja`, `REEMBOLSO` | Reembolso | Finca o caja responsable | `ACTIVO` / `ANULADO` |
| `ENTREGA_DOCUMENTOS_CAJA` | `liquidacion_salida` | Acta de entrega de vales/facturas a caja | Finca o caja receptora | `activo` lógico; futura anulación documentada |
| `ARQUEO_CAJA` | `arqueo_caja` | Acta de arqueo de caja | Finca o caja responsable | `ABIERTO` / `CERRADO` |
| `ACTA_RESPONSABILIDAD_CAJA` | `acta_responsabilidad_caja` | Acta de responsabilidad material | Finca o caja responsable | `ACTIVA` / `CERRADA` |

`ENTREGA_DOCUMENTOS_CAJA` recibe desde V40 el campo
`liquidacion_salida.numero_documento`. Hasta su emisión por el motor común
permanece nulo para registros históricos; no se permite construir un número
oficial a partir de su UUID.

## Documentos de Banco

| Código de forma | Tabla / tipo origen | Nombre operativo | Alcance inicial recomendado | Estado documental |
|---|---|---|---|---|
| `ENTREGA_BANCO` | `entrega_banco` | Comprobante de entrega de efectivo / depósito al banco | Finca, caja originadora o punto de depósito | `activo` lógico; futura anulación documentada |
| `CHEQUE` | `cheque_transferencia`, `CHEQUE` | Cheque emitido | Finca o cuenta bancaria | `EMITIDO` / `COBRADO` / `ANULADO` |
| `TRANSFERENCIA_BANCARIA` | `cheque_transferencia`, `TRANSFERENCIA` | Orden de transferencia bancaria | Finca o cuenta bancaria | `EMITIDO` / `COBRADO` / `ANULADO` |
| `CONCILIACION_BANCARIA` | `conciliacion_bancaria` | Acta de conciliación bancaria | Finca o cuenta bancaria y período | `ABIERTA` / `CERRADA` |

`ENTREGA_BANCO` recibe desde V40 el campo
`entrega_banco.numero_documento`. La referencia bancaria o boleta de depósito
es una evidencia externa y no reemplaza este consecutivo interno.

## Elementos que no son formas independientes

| Elemento | Motivo |
|---|---|
| `movimiento_caja` (`COBRO_EFECTIVO`, `VUELTO_EFECTIVO`, `ENTREGA_BANCO`, `APERTURA_CAJA`, `CAMBIO_DENOMINACION`, `DOCUMENTO_CAJA_INGRESO`, `DOCUMENTO_CAJA_EGRESO`) | Libro auxiliar de movimientos; debe enlazar al documento origen, no emitir otra forma. |
| `movimiento_caja_denominacion` y `saldo_caja_denominacion` | Detalle físico de billetes y saldo, sin entidad documental propia. |
| `arqueo_caja_denominacion` | Anexo del acta de arqueo, no una forma separada. |
| `fondo_caja_autorizado` | Parámetro/autorización de control; si la entidad exige una resolución impresa, se modelará como forma nueva, no mediante esta tabla. |
| `incidencia_arqueo_caja` | Expediente de control asociado al arqueo; su campo manual `expediente` no debe presentarse como consecutivo oficial hasta definir su forma específica. |

## Compatibilidad y reglas de base de datos

La migración `V40__numeros_documentales_liquidacion_y_entrega_banco.sql` añade
`numero_documento` como nullable a las dos cabeceras que no tenían consecutivo.
Sus índices únicos parciales son `(finca_id, numero_documento)` y solo aplican
cuando existe un número: así se preservan íntegramente los datos históricos y
se impiden duplicados en documentos nuevos.

El motor no debe modificar números ya emitidos, reutilizar anulados ni asignar
uno al descargar un PDF. La reserva, la creación de la cabecera y el registro
de emisión deben quedar en la misma transacción de negocio.
