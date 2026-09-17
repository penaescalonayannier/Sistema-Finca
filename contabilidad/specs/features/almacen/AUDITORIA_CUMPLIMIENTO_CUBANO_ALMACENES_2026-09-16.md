---
document: auditoria-cumplimiento-cubano-almacenes
version: 1.0.0
status: findings-and-remediation-plan
review_date: 2026-09-16
scope: [almacenes, inventario, documentos-SC, frontend, backend, auditoria]
---

# Auditoría de cumplimiento cubano — almacenes e inventarios

## Dictamen ejecutivo

El sistema ya tiene una base de control útil: finca y almacén se relacionan,
las salidas múltiples producen un único vale, las entradas de producción se
canalizan al almacén receptor, hay movimientos de existencias, tarjetas de
estiba en PDF y numeración separada de facturas, vales y producción terminada.

No obstante, **no debe declararse todavía cumplimiento integral** del
Subsistema de Inventarios. Los controles documentales SC-2-04, SC-2-09,
SC-2-15 y SC-2-16 no existen aún como expedientes persistentes completos; el
SC-2-14 es una buena salida física, pero no sustituye el Submayor SC-2-13; y la
auditoría de las operaciones críticas todavía debe completarse a nivel de
documento, aprobación y excepción.
Las brechas P0 de esta matriz deben cerrarse antes de sostener una auditoría
formal sobre las operaciones nuevas.

## Referencia normativa y criterio usado

La [Resolución 11/2007 del MFP](https://es.scribd.com/document/887733027/MFP-Resolucion-No-11-de-2007-Conduce-Remision),
publicada en la Gaceta Oficial No. 15 de 2007, establece los datos de uso
obligatorio de los modelos SC-2-04, SC-2-06, SC-2-08, SC-2-09, SC-2-12,
SC-2-13, SC-2-14, SC-2-15 y SC-2-16. La
[Resolución 60/2011 de la Contraloría](https://www.gacetaoficial.gob.cu/sites/default/files/go_x_013_2011.pdf)
exige control interno basado, entre otros, en legalidad, división de funciones,
fijación de responsabilidades, cargo y descargo, información, supervisión y
autocontrol.

La revisión es estática de código y contrato: no acredita firmas manuscritas,
permisos reales de usuarios, ni saneamiento de información histórica. La
entidad debe aprobar además su procedimiento interno, responsables, copias,
custodia y niveles de autorización, que son aspectos que los modelos permiten
adaptar a su organización pero no eliminar.

## Evidencia revisada

| Área | Evidencia técnica actual |
|---|---|
| Rutas físicas | `AlmacenController`, `MovimientoStockController`, `FincaProductoController`, `SalidaController` y `ProduccionTerminadaController`. |
| Servicios críticos | `SalidaServiceImpl`, `MovimientoStockServiceImpl`, `AlmacenFincaProductoServiceImpl`, `AlmacenServiceImpl`, `ProduccionTerminadaServiceImpl`. |
| Documentos/PDF | `FacturaPdfService`, `ProduccionTerminadaPdfService`, `TarjetaEstibaPdfService`, `TarjetaEstibaFincaPdfService`. |
| Interfaz | `DetalleAlmacen.vue`, `FincaProductoList.vue`, `CrearSalida.vue`, `SalidaList.vue`, `ProduccionTerminadaList.vue`, `HistorialStock.vue`. |
| Auditoría transversal | `AuditoriaServiceImpl`, `AuditoriaController` y `AuditoriaTransaccionalService`. Durante esta revisión se añadió trazado posterior al commit para movimiento de inventario y movimientos de caja; deben extenderse a los documentos y estados restantes. |

## Matriz de cumplimiento y brechas

| Modelo / regla | Evidencia implementada | Estado | Brecha objetiva y corrección necesaria | Prioridad |
|---|---|---|---|---|
| SC-2-06, entrega de producción terminada | `POST /api/almacen/{id}/entrada-produccion-terminada`; `ProduccionTerminada` persiste número PT, lote, centro de costo, costo, importe, saldo y snapshots; PDF específico. La creación directa está rechazada. | Parcial alto | No hay estado de emisión/anulación, autorización ni bitácora de quién emitió/aprobó. `PUT` y `DELETE` aún permiten modificar/anular la operación económica sin un documento inverso con motivo y aprobación. | P0 |
| SC-2-08, vale de entrega/devolución | `salida-multiple` valida almacén, finca, existencia y trabajadores; crea un solo encabezado `Salida`, líneas e identificación VALE; hay PDF. La salida directa de almacén se rechaza. | Parcial alto | El encabezado/líneas no preservan todos los snapshots oficiales (receptor/cargo/centro de costo/lote/roles) y la edición/anulación del vale sigue siendo una mutación económica. Se requiere reversión vinculada, motivo y autorización, no reescritura. | P0 |
| SC-2-12, factura | `Salida.numero` FAC por finca/año, restricción de unicidad y PDF. | Parcial | Falta congelar los datos comerciales y contables que muestra el PDF al emitir; la modificación de una salida puede recalcular precios y líneas históricas. Deben coexistir estado, reversión y trazabilidad de cobro. | P1 |
| SC-2-04, informe de recepción | Entrada por almacén permite `ENTRADA_FACTURA` y exige referencia; se registra movimiento físico. | Bajo | No existe cabecera/líneas de recepción: proveedor, fecha y número del documento externo, cantidades recibidas, costo/importe, diferencias/reclamación, responsables de recepción e inventario, estado y PDF. No debe guardar el número fuente solo como texto. | P0 |
| SC-2-11, conduce | La entrada por almacén recibe `numeroConduce` y la producción no puede pasar por la entrada genérica. | Bajo | No existe conduce estructurado ni vínculo de transportista, origen/destino, entrega/recepción, bultos, fecha y firmas. Implementar como documento fuente de recepción/despacho, no como observación. | P1 |
| SC-2-09, transferencia | `POST /api/almacen/{id}/transferir` valida origen/producto y crea movimientos de salida y entrada. | Bajo | Se incrementa el destino inmediatamente. Falta cabecera, consecutivo, líneas, despacho, estado `EN_TRANSITO`, confirmación del receptor, diferencias y reversión. El destino no debe disponer de la mercancía hasta confirmar la recepción. | P0 |
| SC-2-14, tarjeta de estiba | PDF por almacén y consolidado por finca: período, producto, unidad, movimientos, entradas, salidas, saldo y firmas de roles. El consolidado muestra el almacén originador. | Parcial | La tarjeta por almacén debe tener siempre ubicación física (sección/estante/casilla), código de cuenta/subcuenta/análisis, número de documento fuente persistente y responsable que anota. La variante por finca es reporte gerencial complementario; no sustituye una tarjeta por almacén/producto. | P1 |
| SC-2-13, submayor de inventario | `MovimientoStock` conserva producto, finca, almacén, tipo, cantidad, saldo anterior/nuevo, fecha y referencia. | Bajo | No hay submayor valorado por almacén/producto: costo promedio, importes, cuentas/análisis, conciliación contable-física y responsable. Un `MovimientoStock` técnico no equivale por sí solo al SC-2-13. | P0 |
| SC-2-15, hoja de inventario físico | Existen tarjetas y ajuste manual por almacén, con observación obligatoria y no-negatividad. | Bajo | Falta proceso de conteo: congelar/listar muestra, registrar conteo ciego, comparar físico/submayor, sobrante/faltante, responsables y PDF firmado. No puede usarse el ajuste directo como sustituto del conteo. | P0 |
| SC-2-16, ajuste de inventario | `POST /api/movimiento-stock/ajuste` exige tipo, almacén, cantidad positiva y motivo; actualiza físico de almacén/finca y deja movimiento. | Parcial bajo | Falta expediente de ajuste consecutivo y aprobado, vínculo a SC-2-15, concepto normalizado, cantidad/costo/importe/saldo, roles y PDF. El endpoint sólo debe aceptar una solicitud aprobada, nunca ser la autorización en sí. | P0 |
| Existencia inicial / altas | La UI evita establecer existencia inicial al asignar producto sin almacén; hay productos por almacén. | Parcial | La ruta `/{almacenId}/asignar-producto` conserva `stockInicial`; debe restringirse a migración/apertura y exigir acta de apertura o SC-2-16, responsable y autorización. | P1 |
| Inmutabilidad / reversión | Se registran movimientos de stock con referencia y se rechazan diversos bypasses de finca/almacén. | Parcial | Las entidades primarias se pueden editar o desactivar y las tablas de movimientos no tienen protección de BD contra `UPDATE`/`DELETE`. Incorporar estados, documentos inversos, motivo, aprobador, referencias de reversión e inmovilidad de movimientos ya contabilizados. | P0 |
| Concurrencia y saldos | Validaciones previas de stock y control de consecutivo con bloqueo en numeración. | Parcial | La lectura y escritura de stock no demuestran bloqueo pesimista/optimista de la fila física para operaciones simultáneas. Añadir versión o `SELECT ... FOR UPDATE`, prueba de dos salidas/ajustes/transferencias concurrentes y restricción de saldo no negativo. | P0 |
| Segregación de funciones | Hay autenticación general; `SecurityConfig` reserva explícitamente solo Usuarios a ADMIN y el resto está autenticado. | Bajo | Emitir, recibir, aprobar ajuste, anular, contabilizar y consultar auditoría necesitan permisos distintos y configurables. Debe prohibirse autoaprobación, especialmente para ajuste, transferencia y anulación. | P0 |
| Auditoría de sistema | Tabla `auditoria` guarda usuario, IP, acción, valores previos/nuevos y fecha; vista/controlador disponibles. `AuditoriaTransaccionalService` ya publica, tras confirmar, eventos de `MOVIMIENTO_STOCK` y de caja. | Parcial | El movimiento auditado no sustituye el evento documental: faltan emisión, aprobación, rechazo, anulación, impresión y cambios de estado de recepción, vale, factura, producción y transferencia. Además el servicio de auditoría atrapa el error y continúa. Registrar obligatoriamente esas acciones, alertar y no silenciar falla de auditoría crítica. | P0 |
| Informes y trazabilidad | Kardex/tarjetas/PDF y consulta de movimientos existen; los consecutivos se consultan por finca/año. | Parcial | Reportes deben identificar inequívocamente documento y número, usuario/fecha de emisión, filtros y fuente. Añadir informe de excepciones: stock finca distinto de suma de almacenes, movimientos sin documento, documentos sin movimientos y movimientos sin auditoría/asiento. | P1 |

## Controles positivos confirmados

1. Las rutas de finca para stock absoluto, producción, entrada por factura,
   conduce y ajuste lanzan conflicto y fuerzan el almacén físico.
2. La producción terminada ya no se crea por su endpoint genérico; se registra
   desde el almacén receptor y se identifica con consecutivo PT persistido.
3. La salida física genérica de almacén se rechaza: las salidas se canalizan por
   el vale/factura de salida múltiple, incluso para una sola línea.
4. La salida múltiple controla que las líneas pertenezcan al almacén/finca, que
   no haya cantidades negativas y que las entregas a trabajadores cuadren por
   producto.
5. Las tarjetas de estiba no mutan inventario y separan la lectura por almacén
   de la consulta consolidada de finca.

## Diseño mínimo de auditoría que debe implementarse

La tabla transversal existente puede reutilizarse, pero no basta con que sea
opcional. Cada servicio transaccional debe emitir una entrada inmutable con:

| Evento | Entidad auditada | Datos mínimos |
|---|---|---|
| Emisión | Recepción, producción, vale, factura, transferencia o ajuste | número, finca, almacén, líneas/saldos snapshot, usuario, IP, fecha y resultado. |
| Aprobación/rechazo | Ajuste, transferencia, anulación | solicitante, aprobador distinto, motivo, fecha, estado anterior/nuevo. |
| Reversión/anulación | Documento primario y documento inverso | vínculo al original, causa, cantidades/costos, responsable y validación de operaciones posteriores. |
| Impresión/reimpresión | Todo PDF oficial | documento/número, usuario, fecha, versión/reimpresión; nunca genera un nuevo número. |
| Excepción | Conciliación, asiento o auditoría fallidos | operación original, error, fecha, responsable de resolver y estado de resolución. |

Para operaciones que alteren inventario o contabilidad, una falla al grabar la
auditoría debe dejar la operación en estado pendiente/error visible o revertirse
en la misma transacción. Es inaceptable tratarla sólo como `stderr` porque se
pierde la evidencia requerida por la supervisión.

## Plan de corrección por orden seguro

1. **P0 — Expedientes y permisos.** Crear cabeceras/líneas/estados para
   SC-2-04, SC-2-09, SC-2-15 y SC-2-16; añadir roles de emisor, receptor,
   aprobador, inventario y contabilidad. Migrar ajustes actuales a solicitudes
   pendientes de aprobación, sin borrar su historial.
2. **P0 — Inmutabilidad.** Bloquear edición económica de `Salida` y
   `ProduccionTerminada`; reemplazarla por reversión/rectificación vinculada.
   Proteger `movimiento_stock` de actualización/borrado a nivel de aplicación y
   base de datos, salvo procedimiento administrativo controlado.
3. **P0 — Transferencia y concurrencia.** Aplicar flujo despacho → tránsito →
   recepción y bloqueo de la fila de existencias dentro de cada operación.
4. **P0 — Auditoría exigible.** Integrar `IAuditoriaService` en los servicios
   críticos y crear tablero de eventos/errores de auditoría y conciliación.
5. **P1 — Submayor y estiba.** Implementar SC-2-13 valorado y conciliación;
   completar ubicación, cuentas/análisis y documento fuente en SC-2-14.
6. **P1 — Recepción.** Estructurar factura/conduce y su recepción, conservando
   el número externo sin usarlo como consecutivo interno.

## Pruebas de aceptación que bloquean el despliegue

- Dos usuarios no pueden consumir o ajustar simultáneamente más existencia de
  la disponible.
- Una transferencia no aumenta el disponible del destino antes de la recepción
  confirmada; una diferencia queda en acta y auditoría.
- Un ajuste no altera stock hasta tener conteo, documento SC-2-16 y aprobación
  por usuario diferente del emisor.
- Cada movimiento del período puede llevar a documento fuente, número, usuario
  y evento de auditoría; y cada documento activo tiene sus movimientos.
- El saldo de cada `FincaProducto` coincide con la suma de sus almacenes; los
  casos históricos que no coincidan se listan y regularizan por documento, no
  editando la base.
- El PDF reimpreso conserva snapshots y número del documento, y deja trazada la
  reimpresión sin consumir consecutivo.

## Relación con auditorías anteriores

`AUDITORIA_MUTACIONES_STOCK_SC2.md` conserva el inventario detallado de rutas
de una revisión previa. Este informe actualiza su conclusión para el código
vigente: varios bypasses P0 ya se cerraron, pero subsisten las brechas
documentales, de segregación, concurrencia e integración de auditoría indicadas
arriba. Antes de modificar rutas, actualizar ambos documentos y las pruebas de
contrato para que la evidencia siga siendo verificable.
