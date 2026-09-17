---
document: registro-formas-numeradas-recomendacion
version: 1.0.0
status: analizado-pendiente-de-implementacion
review_date: 2026-09-17
scope: [contabilidad, almacen, caja, banco, auditoria]
---

# Registro de formas numeradas por tipo documental

## Decisión funcional recomendada

Cada documento oficial debe pertenecer a una **forma numerada** y tener una
serie consecutiva independiente. Una Factura nunca comparte consecutivo con un
Vale, una Producción Terminada, un Informe de Recepción o un documento de Caja.

La numeración es una evidencia de control: se asigna exclusivamente en el
servidor al emitir el documento, se conserva ante una anulación y nunca se
reutiliza. Una reimpresión o un PDF consolidado muestra el número ya emitido y
no consume otro.

Esta recomendación se apoya en el requisito de comprobantes consecutivamente
numerados, detallados y trazables, y en la exigencia de número consecutivo para
las facturas. La definición operativa de cada serie debe quedar aprobada por la
entidad y su área económica.

## Estado actual analizado

El sistema ya dispone de una base de numeración segura por
`finca + tipo + año` en `configuracion_numeracion`, con bloqueo transaccional.
Actualmente la usan Facturas, Vales, Recibos, Producciones Terminadas, Informes
de Recepción, Transferencias de Almacén, Conteos Físicos y Ajustes de Inventario.

La fecha de corte de la serie actual es la fecha de emisión del servidor
(`LocalDate.now()`), no la fecha económica registrada por el usuario. Esa regla
debe declararse por forma para impedir que una fecha retroactiva consuma una
serie que no le corresponde.

La vista **Consecutivos documentales** solo muestra Factura, Vale, Producción,
Recepción y Transferencia. Por tanto, Recibo, Conteo Físico y Ajuste de
Inventario no aparecen aún en el control visual. Los documentos de Caja y Banco
mantienen en varios casos identificadores técnicos o series globales separadas,
en vez de formar parte de un registro uniforme de formas.

Los asientos contables disponen de un consecutivo propio diario; ese control
debe permanecer separado, pero visible desde el registro general como una forma
documental de contabilidad.

La verificación vigente no es suficiente para un control formal: para Recibo,
Conteo Físico y Ajuste no consulta correctamente los documentos fuente, y una
anulación o hueco justificado aparecería como fallo porque no existen estados
de emisión. Además, Recibo y Conteo Físico tienen actualmente unicidad global
del número, aunque su contador sea por finca; eso debe corregirse antes de que
dos fincas puedan emitir la misma serie.

## Modelo objetivo

| Componente | Responsabilidad |
|---|---|
| Forma numerada | Catálogo: código, nombre, modelo, prefijo, longitud, reinicio anual o continuo, alcance, responsable autorizado y vigencia. |
| Serie de la forma | Serie vigente según entidad, finca, almacén, caja o banco; incluye rango inicial/final y modo electrónico o preimpreso. |
| Libro de emisiones | Evidencia inmutable de cada número: documento origen, emisión, reimpresión, anulación, usuario, fecha, motivo y versión/snapshot impreso. |
| Reserva segura | Obtiene el consecutivo dentro de la misma transacción del documento y protege concurrencia en base de datos. |
| Anulación | Mantiene reservado el número, con motivo, fecha, usuario y vínculo al documento que revierte o sustituye. |
| Auditoría | Reporta emitidos, anulados, rangos asignados, pendientes, duplicados, huecos justificados e integridad. |

Los UUID, identificadores de movimientos de stock o de caja y numeraciones de
renglones son técnicos: no sustituyen el consecutivo de una forma oficial ni
deben consumirlo automáticamente.

Cada emisión debe tener estado `RESERVADO`, `EMITIDO`, `ANULADO` o
`INUTILIZADO`; una reimpresión es un evento de la emisión, no un nuevo número.
El motor debe imponer como mínimo unicidad de `(serie, correlativo)` y un único
vínculo activo entre un documento y su emisión. Las operaciones de emisión
deben usar una clave de idempotencia para que un reintento de la API no cree dos
documentos ni consuma dos números.

## Formas iniciales a registrar

1. Factura comercial o digital.
2. Vale de salida.
3. Producción terminada.
4. Informe de recepción.
5. Transferencia entre almacenes.
6. Conteo físico.
7. Ajuste de inventario.
8. Recibo de cobro.
9. Comprobante de ingreso a caja.
10. Comprobante de entrega o depósito al banco.
11. Arqueo de caja.
12. Acta de responsabilidad de caja.
13. Asiento contable.
14. Cheque.
15. Transferencia bancaria.
16. Conciliación bancaria, si se adopta como acta oficial.
17. Acta de entrega de documentos o valores a caja.

Los cinco tipos que hoy comparten `documento_caja` —recibo de efectivo, vale de
pago menor, anticipo, liquidación de anticipo y reembolso— requieren formas o
series independientes. No es válido que compartan un único contador por ser
documentos de naturaleza distinta.

Los reportes, tarjetas de estiba, kardex y PDFs consolidados no son formas
nuevas: deben indicar fecha/hora, filtros y documentos fuente, sin reservar un
consecutivo adicional.

## Alcance de una serie

El alcance debe poder configurarse por forma, no quedar impuesto para todas las
formas por finca:

- **Factura:** entidad o punto emisor/finca, según la política aprobada.
- **Documentos de almacén:** finca o almacén emisor.
- **Producción terminada:** finca o unidad productiva.
- **Caja y banco:** caja o finca responsable.
- **Asiento contable:** entidad y libro/periodo definido para la contabilidad.

La forma debe permitir reinicio anual o consecutivo continuo. También debe
distinguir series electrónicas de rangos de formularios preimpresos, sin
mezclarlos.

## Brechas que se deben cerrar

- Incorporar Recibo, Conteo Físico y Ajuste de Inventario en la pantalla de
  consecutivos.
- Corregir las restricciones de unicidad global de Recibo y Conteo Físico para
  que coincidan con el alcance real de sus series, sin alterar documentos
  históricos.
- Convertir la configuración actual en un catálogo de formas y series
  configurables, manteniendo compatibilidad con los números existentes.
- Incorporar Entrada a Caja, Entrega al Banco, Arqueo y Acta de Responsabilidad
  al registro uniforme de formas numeradas.
- Dar número persistente, estado, reversión documentada y PDF a Entrega al
  Banco y al acta de entrega de vales/facturas a Caja, que hoy no tienen número
  documental propio.
- Crear el libro persistente de emisiones, anulaciones y reimpresiones.
- Evitar corrección manual de contadores, reutilización y renumeración de
  documentos emitidos.
- Inicializar las series desde los máximos históricos y reportar conflictos sin
  renumerar silenciosamente documentos ya emitidos.

## Orden de implementación recomendado

1. Crear el catálogo, series y libro de emisiones; migrar los contadores y
   documentos existentes sin cambiar sus números visibles.
2. Adaptar Factura, Vale, Producción Terminada, Informe de Recepción,
   Transferencia, Conteo, Ajuste y Recibo al motor común.
3. Integrar los comprobantes de Caja, Entrega al Banco, Arqueo, Actas, Cheques,
   Transferencias y Conciliaciones, separando las series de cada tipo de caja.
4. Ampliar la vista de consulta con filtros por forma, alcance, serie, período,
   estado e informe de integridad descargable.
5. Añadir pruebas de concurrencia, primera emisión anual, anulación,
   reimpresión, rangos preimpresos y despliegue de migración en las demás PC.

## Regla de despliegue

La implementación debe usar migraciones nuevas, progresivas y deterministas;
nunca se modifica una migración ya aplicada ni se renumeran silenciosamente
documentos históricos. El actualizador vigente ejecuta los SQL con `psql`, no
mantiene historial ni checksum de migraciones, por lo que antes de implantar se
debe añadir un registro versionado de aplicación o adoptar Flyway de forma
controlada.

Antes de activar el nuevo motor en cada PC se requiere: respaldo verificable,
validación de base de datos objetivo, detección de duplicados/números no
interpretables/documentos sin alcance, importación histórica sin renumerar e
inicialización de cada serie desde su máximo válido. Debe quedar un acta técnica
por PC con commit instalado, fecha, operador, respaldo, resultado de prechequeo
y máximos iniciales. La contingencia será restaurar el respaldo o publicar una
migración compensatoria; nunca ejecutar un rollback automático sobre datos en
producción.
