---
document: auditoria-mutaciones-stock-sc2
version: 1.0.0
status: findings
review_date: 2026-09-16
scope: [backend, frontend]
---

# Auditoría de puntos que mutan existencias

## Alcance y método

Esta auditoría estática identifica rutas HTTP, handlers y servicios que, al
momento de la revisión, actualizan `FincaProducto.stock` o
`AlmacenFincaProducto.stock`, crean un `MovimientoStock`, o revierten una
salida/producción. También distingue las rutas expuestas que no tienen un
consumidor localizado en `mi-proyecto-vue/mi-proyecto/src`; esas rutas son un
riesgo porque pueden ser invocadas por clientes antiguos, scripts o llamadas
directas aunque la interfaz actual no las muestre.

No sustituye pruebas integradas ni una revisión de datos históricos. Las reglas
normativas y el modelo objetivo están en
[`CONTROL_DOCUMENTAL_SC2.md`](./CONTROL_DOCUMENTAL_SC2.md).

## Criterio documental

| Operación física | Documento mínimo esperado | Regla de control |
|---|---|---|
| Producción acabada entra a almacén | SC-2-06 | Documento y stock se confirman en una sola transacción; snapshot de cantidades, costo, saldo, orden/lote y responsables. |
| Salida o devolución de almacén | SC-2-08 | Vale consecutivo, receptor/centro de costo/lote, líneas, saldos y roles de entrega, recepción, inventario y contabilidad. |
| Transferencia entre almacenes | SC-2-09 | Despacho y recepción separados; estado `EN_TRANSITO`, confirmación de destino y diferencias justificadas. |
| Entrada de proveedor | SC-2-04 y, cuando corresponda, factura/conduce | Recepción, proveedor, documento fuente, cantidades/costo/importe/saldo y responsables. |
| Ajuste por conteo/diferencia | SC-2-15 + SC-2-16 | Conteo físico, motivo, autorización, costo/importes y firmas; no una edición libre de stock. |
| Existencia inicial | Acta/ajuste de apertura definido por la entidad | Motivo, fecha, autorización, responsable y saldo inicial; no solo un campo editable. |

## Matriz de mutaciones — rutas HTTP

| Punto expuesto | Tipo que registra hoy | Documento actual / requerido | Riesgo o bypass observado | Control esperado y prioridad |
|---|---|---|---|---|
| `POST /api/produccion-terminada` | `ENTRADA_PRODUCCION`; incrementa finca y, si se invoca flujo de almacén, almacén | Genera registro `ProduccionTerminada` y PDF; debe ser SC-2-06 completo | La creación directa permite producción sin almacén. La entidad no persiste consecutivo, orden/lote separado, cantidades entregada/recibida, costo real, importes/saldos históricos, firmas/estado. | **P0.** Hacer que la emisión SC-2-06 sea el único camino de producción o exigir almacén; guardar snapshots y flujo de emisión/recepción. |
| `PUT /api/produccion-terminada/{id}` | Entrada adicional o `AJUSTE_EDICION` / decremento según diferencia | Corrección mediante documento inverso, no edición del primario emitido | Cambia cantidades, responsables y observaciones de una producción activa y altera stock. No conserva versión ni autorización. | **P0.** Bloquear edición económica tras emisión; corrección por reversión/ajuste vinculado y con historial. |
| `DELETE /api/produccion-terminada/{id}` | `REVERSION_PRODUCCION` o `DEVOLUCION` | Anulación/reversión referida al SC-2-06 | Soft-delete y reversa stock, pero no exige motivo, aprobador, consecutivo de reversión ni comprueba consumo posterior por lote/documento. | **P0.** Anulación documental trazable y regla para impedir o escalar reversión de existencias ya consumidas/transferidas. |
| `POST /api/almacen/{almacenId}/entrada-produccion-terminada` | `ENTRADA_PRODUCCION` en almacén y finca | Debe ser SC-2-06 | Es el camino correcto conceptualmente, pero el request de backend solo declara cantidad y dos trabajadores/observaciones, mientras la interfaz intenta enviar lote, centro de costo y costo unitario. Aun sin ese desfase, faltan snapshots y estados. | **P0.** Alinear contrato y exigir todos los campos SC-2-06 antes de actualizar stock. |
| `POST /api/finca-producto/entrada-produccion` | `ENTRADA_PRODUCCION` solo en consolidado de finca | SC-2-06 | La vista `EntradaProduccion.vue` lo consume. Incrementa stock sin almacén, documento SC-2-06, orden/lote, recepción ni responsables. Compite con el flujo nuevo de almacén. | **P0.** Retirar de UI y de API pública o redirigir internamente al flujo SC-2-06 con almacén obligatorio. |
| `POST /api/almacen/{almacenId}/entrada` con `ENTRADA_PRODUCCION` | Rechazado | SC-2-06 | Control positivo: el handler ya rechaza producción genérica. | Mantener la prohibición y probar que no puede eludirse por valores de enum o clientes antiguos. |
| `POST /api/almacen/{almacenId}/entrada` con `ENTRADA_FACTURA` | `ENTRADA_FACTURA` en almacén y finca | SC-2-04 + factura | La UI lo usa. Solo exige número de factura y descripción; no persiste proveedor, fecha fuente, costo, importe, saldo documental ni firmas de jefe/recepción/contabilidad. | **P1.** Crear recepción documental por proveedor, con snapshot; número de factura no debe vivir solo dentro de descripción. |
| `POST /api/almacen/{almacenId}/entrada` con `ENTRADA_CONDUCE` | `ENTRADA_CONDUCE` en almacén y finca | SC-2-04/SC-2-11 según operación | La UI lo usa. Exige número de conduce, pero lo concatena a una descripción. Sin proveedor/transportista, costo, importes, recepción o vínculo estructurado al conduce. | **P1.** Documento fuente estructurado y recepción controlada. |
| `POST /api/almacen/{almacenId}/entrada` con `ENTRADA_AJUSTE` | `ENTRADA_AJUSTE` | SC-2-15 + SC-2-16 | La interfaz permite entrada de ajuste por este camino y el handler no exige observación, motivo de conteo, autorización ni acta. Compite con `/api/movimiento-stock/ajuste`. | **P0.** Eliminar/encaminar a un único proceso SC-2-16 aprobado. |
| `POST /api/movimiento-stock/ajuste` | `ENTRADA_AJUSTE` / `SALIDA_AJUSTE`, altera almacén y finca | SC-2-15 + SC-2-16 | La vista `FincaProductoList.vue` lo consume. Exige observación y valida no negativo, pero no posee acta de conteo, concepto normalizado, costo/importe, responsables, aprobación o PDF SC-2-16. | **P0.** Convertirlo en el único flujo de ajuste, con expediente/acta, estados y PDF. |
| `POST /api/finca-producto/{id}/ajuste` | `AJUSTE_MANUAL` en finca | SC-2-15 + SC-2-16 | Ruta pública sin consumidor frontend localizado. Ajusta el consolidado sin seleccionar almacén: puede romper la regla «stock finca = suma de almacenes». | **P0.** Deshabilitar para clientes externos o exigir almacén y delegar al ajuste documental único. |
| `PUT /api/finca-producto/stock` | `AJUSTE_MANUAL` | SC-2-15 + SC-2-16 | Ruta pública sin consumidor frontend localizado. Asigna el stock absoluto sin motivo obligatorio ni almacén, y puede desincronizar el inventario consolidado. | **P0.** Retirar/denegar en producción; sustituir por ajuste con diferencia, motivo y autorización. |
| `POST /api/finca-producto/{id}/entrada-factura` | `ENTRADA_FACTURA` solo en finca | SC-2-04 + factura | Ruta pública sin consumidor frontend localizado; no toca almacén y no conserva recepción oficial. | **P0.** Descontinuar o redirigir al flujo de entrada por almacén. |
| `POST /api/finca-producto/{id}/entrada-conduce` | `ENTRADA_CONDUCE` solo en finca | SC-2-04/SC-2-11 | Ruta pública sin consumidor frontend localizado; no toca almacén y falta documento estructurado. | **P0.** Descontinuar o redirigir al flujo de entrada por almacén. |
| `POST /api/finca-producto/asignar` con `stock > 0` | `STOCK_INICIAL` en finca | Acta/ajuste inicial | La interfaz `FincaProductoList.vue` lo usa. Registra movimiento, pero no exige almacén ni documento de apertura; provoca stock de finca no distribuido por almacenes. | **P0.** Separar «asignar catálogo» de «incorporar existencia inicial» y forzar almacén + acta. |
| `POST /api/almacen/{almacenId}/asignar-producto` con `stockInicial > 0` | `ENTRADA_AJUSTE` en almacén y finca | Acta/ajuste inicial | Ruta pública sin consumidor frontend localizado. La operación incorpora físico sin motivo, autorización o documento de apertura. | **P1.** Mantener solo para migración inicial restringida o usar expediente SC-2-16 de apertura. |
| `POST /api/salida` | Según destino: `SALIDA_AUTOCONSUMO`, `SALIDA_COMEDOR` o `SALIDA_VENTA` | SC-2-08 o factura/SC-2-10/SC-2-11 si aplica | Las vistas `DetalleAlmacen.vue` y `CrearSalida.vue` lo usan. Genera vale/factura y número, pero permite edición/anulación posterior; encabezado no conserva todos los campos SC-2-08. En salidas simples con almacén se descuenta ambos saldos. | **P0.** Estructurar SC-2-08, snapshots y estados; prohibir mutación económica luego de emisión. |
| `PUT /api/salida/{id}` | Ajusta salida o `DEVOLUCION` | Documento de corrección vinculado | La vista `CrearSalida.vue` lo usa. Elimina y vuelve a crear ítems, recalcula precio actual y altera stock/deuda; no conserva versión ni documento de modificación. | **P0.** Reemplazar por anulación/reversión + nuevo vale; no recalcular precios históricos. |
| `DELETE /api/salida/{id}` | `DEVOLUCION` | Vale de devolución/reversión SC-2-08 | `SalidaList.vue` lo usa. Conserva encabezado inactivo y devuelve stock, pero no exige motivo/autorización. En salidas históricas de un producto no siempre actualiza stock de almacén; la devolución al almacén solo se hace cuando existe `item.almacenFincaProductoId`. | **P0.** Exigir motivo, documento de reversión y devolver exactamente al origen documentado; bloquear si hay consumo/pago/operación derivada. |
| `POST /api/almacen/{almacenId}/salida-multiple` | Varios movimientos de salida, un vale | SC-2-08 | `DetalleAlmacen.vue` lo usa. Control positivo: un vale y líneas múltiples, valida trabajadores y stock. Aun faltan receptor/centro de costo/lote, firmas/estados y snapshots documentales. | **P1.** Construir encabezado y líneas SC-2-08 completos antes de la emisión. |
| `POST /api/almacen/{almacenId}/salida` | `SALIDA_AUTOCONSUMO` en cualquier destino no reconocido | SC-2-08 | Ruta pública sin llamada frontend localizada. Recibe texto libre `destino`; para trabajador/comedor termina usando servicio genérico y no crea `Salida`, vale, deuda ni receptor documental. Es un bypass directo de salidas. | **P0.** Retirar de API pública o redirigir a `ISalidaService` y exigir documento por destino. |
| `POST /api/almacen/{almacenId}/transferir` | `TRANSFERENCIA_SALIDA` y `TRANSFERENCIA_ENTRADA` | SC-2-09 | `DetalleAlmacen.vue` lo usa. Reduce origen e incrementa destino inmediatamente, no crea entidad/documento consecutivo, no tiene recepción, autorización, firmas ni diferencias. | **P0.** Sustituir por transferencia SC-2-09 de dos fases (`EN_TRANSITO` → `RECIBIDO`). |

## Mutaciones de servicio que no son una ruta propia

| Clase / método | Origen normal | Riesgo a controlar |
|---|---|---|
| `FincaProductoServiceImpl.removerTodosProductosDeFinca` | Método de servicio; no hay controller correspondiente localizado | Marca inactivos sin comprobar saldo. Si un caller futuro lo expone o lo invoca desde borrado de finca, puede ocultar existencias. Debe exigir cero o un proceso de cierre inventarial. |
| `AlmacenFincaProductoServiceImpl.actualizarStock` | Servicio sin endpoint propio localizado | Establece saldo absoluto y crea ajuste genérico sin motivo. Debe mantenerse interno o migrar a SC-2-16. |
| `AlmacenFincaProductoServiceImpl.entrada`, `entradaFactura`, `entradaConduce` | Handler de entrada de almacén y posibles callers internos | Todas deben recibir una referencia documental estructurada; impedir llamadas que solo pasen texto libre. |
| `AlmacenFincaProductoServiceImpl.salida`, `salidaTrabajador`, `salidaComedor` | Endpoint legado `/almacen/{id}/salida` | Descuentan físico y finca sin crear `Salida`/vale; no deben ser públicos. |
| `AlmacenFincaProductoServiceImpl.transferir` / `transferirConDestino` | Endpoint de transferencia y callers futuros | Es transferencia instantánea sin documento. Debe ser interno a una entidad SC-2-09 que controle despacho y recepción. |
| `FincaProductoServiceImpl.decrementarStock` | Reversión/edición de producción | Su uso permitido debe limitarse a documentos de reversión con referencia obligatoria. El overload sin referencia es peligroso para callers futuros. |
| `MovimientoStockServiceImpl.registrar` | Invocado por varios servicios | Persiste un movimiento y, si falla la contabilización, solo registra advertencia y conserva el físico. Definir cola/estado de contabilización y alerta; no dejar diferencia silenciosa entre inventario y contabilidad. |
| Repositorio `AlmacenFincaProductoWriteDataJPARepository.incrementarStock/decrementarStock` | No se encontró consumidor directo actual | Mantener acceso restringido: sus `UPDATE` no crean movimiento documental ni asiento. |

## Bypasses localizados en interfaz y contrato

1. **Dos pantallas de producción:** `EntradaProduccion.vue` llama el endpoint
   histórico de finca, mientras `DetalleAlmacen.vue` llama el endpoint de
   producción terminada de almacén. La primera debe retirarse o convertirse en
   un acceso al mismo asistente SC-2-06.
2. **Contrato de producción desalineado:** `DetalleAlmacen.vue` transmite
   `lote`, `centroCosto` y `costoUnitario`; el request backend inspeccionado
   declara solo `almacenFincaProductoId`, `cantidadTerminada`, trabajadores y
   observaciones. Antes de desplegar, incorporar esos campos de forma
   consistente y cubrirlos con prueba de contrato.
3. **Ajustes duplicados:** la entrada genérica de almacén permite
   `ENTRADA_AJUSTE`, el módulo central ofrece `/movimiento-stock/ajuste`, y la
   API de finca mantiene dos ajustes absolutos/relativos. Debe sobrevivir un
   único proceso SC-2-16.
4. **Salidas duplicadas:** el flujo de `SalidaService` crea vale/factura; el
   endpoint legado de salida de almacén modifica físico sin ello. Debe retirarse
   o delegar al mismo servicio documental.
5. **Transferencia instantánea:** la UI confirma «completada» en el despacho;
   no existe posibilidad de confirmar, rechazar o señalar faltante en destino.
6. **Capa de productos:** `ProductoService.actualizarStock` existe como cliente
   frontend, aunque no se localizó un endpoint de producto que lo soporte ni un
   consumidor. Retirarlo o documentar explícitamente que no es API válida para
   evitar que sea reutilizado como bypass.

## Prioridades de implementación

### P0 — bloquear inconsistencias y operaciones sin soporte documental

1. Unificar producción en SC-2-06 con almacén obligatorio y retirar/redirigir
   `finca-producto/entrada-produccion`.
2. Unificar ajustes en SC-2-16, bloqueando ajustes absolutos de finca y la
   entrada genérica de ajuste.
3. Deshabilitar o adaptar los endpoints de entrada por finca sin almacén y la
   salida directa de almacén sin vale.
4. Reemplazar edición y delete económico de producción/salida por
   anulación/reversión documental con motivo, aprobación y vínculo original.
5. Implementar transferencia SC-2-09 de dos etapas y prohibir el incremento
   inmediato de destino.

### P1 — completar evidencia, auditoría y contabilidad

1. Persistir encabezados/líneas/snapshots de SC-2-04, SC-2-06, SC-2-08 y
   SC-2-09, con números consecutivos por finca/año.
2. Añadir firma/rol/fecha digital, estados, historial y exportación PDF desde
   snapshots para cada documento.
3. Hacer visible y reintentable la contabilización fallida de cada
   `MovimientoStock`; ninguna advertencia debe quedar sin control.
4. Añadir bloqueo/concurrencia para la comprobación y actualización de stock.

### P2 — pruebas y saneamiento

1. Pruebas de contrato frontend-backend para cada endpoint mutante.
2. Pruebas de concurrencia: dos salidas, ajuste contra salida y transferencia
   contra salida del mismo producto.
3. Reporte de registros históricos sin almacén o sin documento y plan de
   regularización sin alterar el historial.
4. Revisión de reglas contables para cada `TipoMovimientoStock` y tablero de
   movimientos sin asiento o con asiento rechazado.

## Checklist de cierre de auditoría

- [ ] Se hizo inventario de endpoints mutantes y se retiraron/autorizaron los
      bypasses P0.
- [ ] Cada documento emitido tiene consecutivo, estado, origen, snapshots,
      usuarios y vínculos de movimiento.
- [ ] Los PDFs de producción, vale y transferencia no usan precio o saldo
      actuales para sustituir valores históricos.
- [ ] Un ajuste parte de inventario físico y genera SC-2-16; no se editan
      saldos directamente.
- [ ] Una transferencia no aumenta destino hasta confirmar recepción.
- [ ] Producción, salida, devolución y anulación mantienen inventario de finca
      igual a la suma de almacenes.
- [ ] Las pruebas de backend y type-check del frontend cubren los contratos
      mutantes y las migraciones nuevas se documentan antes de actualizar PCs.
