---
document: auditoria-consecutivos-documentos
version: 1.0.0
status: findings
review_date: 2026-09-16
scope: [backend, migrations, pdf]
---

# Auditoría de consecutivos y documentos emitidos

## Alcance

Esta revisión distingue el número de una **evidencia primaria persistida** del
contador visual de un renglón o de un PDF que se arma bajo demanda. Se revisaron
entidades, servicios, repositorios y migraciones presentes al 2026-09-16,
incluida la migración pendiente `V26__documento_produccion_snapshot.sql`.

Los campos «seguro en concurrencia» se refieren a la garantía observada en la
base de datos y el código. Una secuencia PostgreSQL es segura para asignar
valores únicos, pero puede dejar huecos al fallar o revertirse una transacción;
un hueco documentado es preferible a reutilizar un número emitido.

## Resumen ejecutivo

- Vales, facturas y recibos de pago usan `configuracion_numeracion` por
  finca/tipo/año, con bloqueo pesimista para filas existentes. Falta una
  restricción única en la tabla `salida` y manejo de la carrera cuando se crea
  por primera vez la configuración anual.
- Producción SC-2-06 ya usa el mismo mecanismo con tipo `PRODUCCION`; desde
  V26 persiste el número y lo protege un índice único por finca. Su uso exige
  que V26 se aplique en todas las PC antes de activar la versión.
- Arqueos, actas de responsabilidad, documentos de caja, cheques/transferencias
  y conciliaciones usan secuencias PostgreSQL globales: son únicas y
  concurrentes, pero no están segmentadas por finca, tipo o año.
- Entregas a banco, liquidaciones de salidas, movimientos de caja, ajustes de
  stock y transferencias no tienen consecutivo documental propio. Las últimas
  dos son brechas críticas para los modelos SC-2-16 y SC-2-09.
- Los PDF consolidados, tarjeta de estiba, kardex y reportes son salidas de
  consulta: no emiten ni guardan un documento nuevo. Deben mostrar la fecha de
  generación y los documentos fuente, pero no inventar un consecutivo oficial.

## Matriz de documentos primarios

| Documento / modelo | Número y persistencia | Unicidad | Concurrencia | Anulación y reutilización | Hallazgo / acción |
|---|---|---|---|---|---|
| Factura de salida, SC-2-12 | `salida.numero`; `INumeracionService`, formato `FAC-AAAA-NNNNN` | La configuración es única por finca/tipo/año; **`salida.numero` no tiene unique DB observado** | Bloqueo pesimista sobre una configuración existente; carrera posible al crear la fila anual por primera vez, sin reintento | Al anular se conserva la salida/número; el contador no baja. Un rollback antes de persistir puede reutilizar porque el incremento se revierte | **P0:** índice único de negocio `(finca, numero)` o equivalente desnormalizado; manejar `unique violation` al crear configuración y reintentar. |
| Vale de salida, SC-2-08 | Igual que factura, prefijo `VALE` y `salida.numero` | Igual: configurador único, falta protección directa en `salida` | Igual | Igual; `PUT /salida` altera documento ya numerado, aunque no renumera | **P0:** además de unicidad, reemplazar edición por reversión y preservar snapshots de las líneas. |
| Recibo de pago de deuda, SC-3-01 / comprobante | `pago_deuda.numero_recibo`; `REC-AAAA-NNNNN` por `INumeracionService` | `numero_recibo` tiene constraint/columna `UNIQUE` global | Igual al configurador: sólido para fila existente, carrera de creación anual no reintentada | No se observó flujo de anulación/reversión del pago; el número se conserva porque no hay delete expuesto revisado | **P1:** añadir estado/anulación o recibo inverso con referencia; número único global es más fuerte que el alcance finca/año, pero debe documentarse. |
| Producción terminada, SC-2-06 | `produccion_terminada.numero_documento`; `PT-AAAA-NNNNN` generado por `INumeracionService` | V26 crea índice único `(finca_id, numero_documento)` cuando no nulo; no es `NOT NULL` | Igual al configurador; el número se asigna antes de guardar en la misma transacción | `DELETE` lógico conserva campo; no debe generar otro. Producciones históricas pueden quedar sin número | **P0:** aplicar V26; hacer el número obligatorio para nuevos documentos y no permitir edición económica del emitido. Mantener «SIN-NÚMERO HISTÓRICO» solo para legado. |
| Transferencia entre almacenes, SC-2-09 | No existe entidad ni número; solo dos `MovimientoStock` y textos de descripción | No aplica | No aplica | No hay documento que anular/relacionar | **P0:** crear cabecera/líneas de transferencia, consecutivo persistente por finca/año, estados `EN_TRANSITO`/`RECIBIDO` y reversión. |
| Ajuste de inventario, SC-2-16 | No existe documento; solo `MovimientoStock.id` UUID | UUID técnico único, no consecutivo de acta | UUID seguro, pero no resuelve numeración documental | No hay anulación documental; se crean nuevos movimientos | **P0:** expediente/acta de ajuste con número consecutivo, motivo, aprobación y vínculo a conteo físico SC-2-15. |
| Informe de recepción de proveedor, SC-2-04 | No existe cabecera documental propia; número de factura/conduce se guarda o concatena como texto | No aplica | No aplica | No aplica | **P1:** no confundir el número externo de factura/conduce con el consecutivo interno del informe de recepción. |
| Arqueo de caja, SC-3-06 | `arqueo_caja.numero`, `arqueo_caja_numero_seq` PostgreSQL | `UNIQUE` global | `nextval` es atómico y seguro | Cierre inmutable; no hay anulación/reutilización. Secuencia puede dejar huecos normales | Correcto para unicidad; **P1:** formato legible y alcance (por ejemplo `ARQ-finca-año-número`) si el procedimiento de la entidad lo exige. Nombre de archivo PDF usa UUID, no el número. |
| Acta de responsabilidad material de caja | `acta_responsabilidad_caja.numero`, secuencia PostgreSQL | `UNIQUE` global | Seguro por secuencia | Se cierra, no se borra ni reutiliza | **P1:** no hay PDF/end-point de impresión localizado; añadirlo si se usará como acta oficial y hacer que el archivo emplee el número. |
| Incidencia de arqueo | UUID técnico y campo manual `expediente` | Un incidente por arqueo (`arqueo_caja_id UNIQUE`); `expediente` no es único | No hay generador de expediente | Estados de resolución; no consecutivo | **P1:** definir si `expediente` es número oficial; si lo es, generar/validar único por finca/año y emitir reporte. |
| Documento de caja: recibo, vale de pago menor, anticipo, liquidación, reembolso | `documento_caja.numero`, `documento_caja_numero_seq` | `UNIQUE` global y columna única | `nextval` seguro | Anulación conserva número; si afectó caja, se prohíbe anular y se exige documento inverso | Funciona como secuencia única, pero mezcla cinco tipos y todas las fincas/años. **P1:** decidir si la normativa interna requiere serie por tipo/finca/año; prefijar e imprimir el tipo. PDF debe nombrarse por número, no UUID. |
| Cheque o transferencia bancaria | `cheque_transferencia.numero`, secuencia PostgreSQL compartida | `UNIQUE` global | `nextval` seguro | Confirmación cambia a `COBRADO` o `ANULADO`, conserva número | **P1:** no se localizó PDF ni numeración separada por cheque/transferencia/finca/año. La referencia bancaria no es el consecutivo interno. |
| Conciliación bancaria | `conciliacion_bancaria.numero`, secuencia PostgreSQL | `UNIQUE` global; además único `(finca, periodo)` | `nextval` seguro | Cierre conserva número; no se reabre | **P1:** no se localizó PDF/acta de conciliación. El número es global, no anual/finca. |
| Entrega de efectivo a banco | Solo UUID `entrega_banco.id` | UUID técnico único | UUID seguro | `activo` permite ocultarla, pero no hay número de comprobante | **P0:** es salida de caja y debe tener número de entrega/boleta, estado, comprobante y PDF; la referencia bancaria no sustituye el consecutivo interno. |
| Liquidación / entrega de vales y facturas a caja | Solo UUID `liquidacion_salida.id`; en reportes aparece sintético `LIQ-<UUID>` | UUID técnico único | La liquidación bloquea ítems para saldo, pero no hay consecutivo documental | No se observó anulación/reversión documental | **P1:** si es acta de entrega a cajera, emitir consecutivo persistente y PDF; no usar un texto derivado del UUID como número oficial. |
| Movimiento de caja / desglose de billetes | UUID `movimiento_caja.id` | UUID técnico único | UUID seguro | Depende del documento origen, sin consecutivo propio | Es libro auxiliar, no necesariamente documento primario. **P2:** mostrar siempre el consecutivo del documento origen y prohibir movimientos sin origen/causa autorizada. |
| Asiento contable automático | `asiento_contable.numero`, formato `AS-AAAAMMDD-NNNN` | Columna `UNIQUE` global | **No seguro:** lee `MAX(numero)` y suma 1 sin bloqueo, secuencia ni reintento; dos transacciones pueden calcular el mismo valor | No se observó anulación formal; el unique causa error y puede dejar movimiento sin asiento (el caller atrapa la excepción y solo avisa en log) | **P0:** reemplazar por secuencia/contador bloqueado y hacer visible/reintentable el fallo; la tabla `configuracion_numeracion_asiento` de V13 existe pero no se usa. |

## PDFs y reportes que no emiten nuevo consecutivo

| Salida PDF | Identidad que muestra | Persistencia / conclusión |
|---|---|---|
| `FacturaPdfService`: factura o vale individual | `salida.numero` | Reimpresión del mismo documento; correcto que no asigne otro número. Debe alimentarse de snapshots para no cambiar datos históricos. |
| PDF consolidado de vales y PDF de vales individuales seleccionados | Números de los vales incluidos | Es una agrupación de impresión, no un nuevo vale. No debe alterar contabilidad ni reservar serie propia; añadir fecha/hora de emisión y selección para trazabilidad si se archiva. |
| `ProduccionTerminadaPdfService` | `numero_documento`, o `SIN-NÚMERO HISTÓRICO` | Tras V26 usa dato persistido; no calcula desde UUID. Correcto para registros nuevos. |
| `ReciboPdfService` | `pago_deuda.numero_recibo` | Reimpresión del recibo persistido; correcto. |
| `ArqueoCajaPdfService` | `arqueo_caja.numero` | Reimpresión del acta persistida; correcto, aunque el nombre de descarga debe usar el número. |
| `DocumentoCajaPdfService` | `documento_caja.numero` | Reimpresión correcta; el endpoint construye el archivo con UUID, no con el número. |
| Tarjetas de estiba, kardex, balance, consolidado de movimientos y reportes de existencias | Parámetros de consulta / documentos fuente | Son reportes bajo demanda, no documentos primarios persistidos. Deben incluir período, finca/almacén, fecha-hora de emisión y fuente de datos; no requieren reservar consecutivo, salvo que la política interna ordene archivar cada emisión. |
| Conciliación y cheque/transferencia | No se localizó endpoint PDF | Brecha funcional: existen números persistidos, pero falta un modelo imprimible que los use. |
| Entrega al banco / liquidación a caja | No se localizó PDF documental | Brecha funcional y de control: tampoco existe número persistente. |

## Observaciones técnicas sobre `INumeracionService`

1. La tabla `configuracion_numeracion` tiene `UNIQUE(finca_id, tipo, anio)` y
   versión optimista; el servicio toma `PESSIMISTIC_WRITE` cuando ya existe la
   fila. Eso serializa números de la misma finca/tipo/año en condiciones
   normales.
2. Si dos solicitudes son el primer documento del año, ambas pueden no hallar
   la fila y tratar de insertarla. La restricción evita duplicar configuración,
   pero una petición puede fallar por violación de unique porque no hay captura
   y reintento.
3. La tabla de destino debe tener su propia constraint de número. `pago_deuda`
   y producción (V26) la tienen; `salida` no. El contador es una ayuda, no la
   última barrera de integridad.
4. El año se toma de `LocalDate.now()`, no de la fecha efectiva que un usuario
   envía. Es una decisión válida si el consecutivo se basa en **fecha de
   emisión**. Debe quedar declarada; no se debe permitir que un documento
   emitido hoy con fecha económica anterior consuma una serie histórica sin una
   política explícita.
5. El contador se actualiza dentro de la transacción del documento. Si toda la
   operación revierte antes de emitirse, el número puede volver a estar
   disponible. Si la entidad exige conservar huecos también ante fallos, use
   una secuencia de base de datos o un registro de números reservados; si no,
   registre que el número solo se considera emitido al confirmar el documento.
6. `verificarIntegridadSecuencia` actualmente solo comprueba que exista la
   configuración: no detecta huecos, duplicados ni números anulados. Debe
   contrastar la serie contra las tablas de documentos y distinguir anulados de
   faltantes indebidos.

## Plan prioritario

### P0

- Crear y aplicar la restricción única para las salidas según su finca y número;
  proteger el alta concurrente del contador con reintento o UPSERT bloqueado.
- Aplicar V26 en todas las PC y exigir `numero_documento` a toda producción
  nueva.
- Implementar consecutivo y documento para SC-2-09 transferencia, SC-2-16
  ajuste y entrega a banco; eliminar los UUID sintéticos como numeración
  visible.
- Sustituir la generación de asiento `MAX + 1` por una secuencia/contador con
  bloqueo. Hacer que las excepciones de asiento queden en una cola/estado
  reintentable, no solo en el log.

### P1

- PDF/acta para cheque-transferencia, conciliación, entrega al banco y acta de
  responsabilidad; los nombres de archivo deben contener el consecutivo
  visible.
- Decidir y documentar por cada familia si su serie es global o por
  finca/tipo/año. Para documentos internos de cada finca se recomienda el
  segundo esquema; para secuencias globales actuales, añadir prefijo de tipo y
  finca al renderizar.
- Implementar anulación/reversión explícita del recibo de deuda y de la
  liquidación de caja, preservando su número original y enlazando el sucesor.

### P2

- Añadir historial de impresión/descarga y hash/versionado solo cuando la
  entidad necesite acreditar qué representación se entregó; no duplicar el
  documento por cada descarga.
- Construir reporte de integridad por serie: emitidos, anulados, históricos sin
  número, duplicados, huecos y contador configurado.

## Checklist de despliegue

- [ ] La migración V26 y toda migración de consecutivos nueva se ejecutó por el
      actualizador en las tres PC; no se modificó una migración aplicada.
- [ ] Se verificó unicidad con dos emisiones concurrentes del mismo tipo/finca.
- [ ] Se verificó la primera emisión de un año sin fallo de carrera.
- [ ] Se anuló un documento y se comprobó que su número continúa reservado y
      visible en la consulta/PDF.
- [ ] Se reimprimieron factura, vale, recibo, producción, arqueo y documento
      de caja confirmando que muestran el mismo número persistido.
- [ ] Se validó que los reportes consolidados no crean ni consumen consecutivos
      de documentos fuente.
