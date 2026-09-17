---
feature: almacen-produccion-documental
version: 1.0.0
status: proposed
priority: critical
depends_on: [almacen, produccion-terminada, salida, movimiento-stock, finca-producto]
last_reviewed: 2026-09-16
---

# Control documental de producción y almacenes (SC-2)

## Propósito

Esta especificación define el comportamiento que deben cumplir las entradas de
producción terminada, las salidas y las transferencias entre almacenes. Su
objetivo es que cada movimiento físico y contable tenga un documento primario
trazable, conservado e imprimible, sin que una reimpresión cambie la evidencia
histórica.

Aplica a los almacenes de cada finca y complementa las especificaciones de
`produccion-terminada`, `salida`, `movimiento-stock` y `finca-producto`.

## Referencias normativas

- Resolución 11/2007 del Ministerio de Finanzas y Precios, publicada en la
  Gaceta Oficial del 12 de febrero de 2007. Pone en vigor los datos de uso
  obligatorio del Subsistema de Inventarios, incluidos los modelos SC-2-06,
  SC-2-08, SC-2-09, SC-2-13, SC-2-14, SC-2-15 y SC-2-16.
  [Consulta de la Gaceta](https://es.scribd.com/document/887733027/MFP-Resolucion-No-11-de-2007-Conduce-Remision).
- Resolución 60/2011 de la Contraloría General de la República, artículo 12:
  separación de tareas y responsabilidades, autorización, documentación,
  registro oportuno, fiabilidad y trazabilidad de las transacciones. Los
  documentos pueden conservarse en formato digital según defina la entidad.
  [Consulta de la Gaceta](https://www.cibercuba.com/s/gacetaoficial/resolucion-60-de-2011-de-contraloria-general-de-la-republica).

La norma define los datos mínimos. La entidad debe definir en su manual de
control interno los roles autorizados, el flujo de aprobación y la conservación
de las copias o archivos digitales.

## Principios no negociables

1. Ninguna entrada, salida, devolución, transferencia o ajuste modifica stock
   sin documento primario y movimiento de inventario vinculados.
2. La emisión guarda una **foto inmutable** de cantidades, costo, importe,
   saldo resultante, responsables y datos maestros mostrados en el documento.
   Un PDF histórico nunca consulta el precio o el saldo actual para sustituir
   esos valores.
3. Una vez emitido, el documento no se elimina ni se renumera. Las correcciones
   se realizan por anulación o documento de reversión vinculado, con motivo y
   responsable.
4. El consecutivo es único por tipo documental, finca y año; se asigna dentro
   de una transacción de base de datos y permanece reservado aunque se anule.
5. Las funciones de solicitar, aprobar, despachar/recibir, anotar inventario y
   contabilizar deben asignarse a roles distintos cuando la organización lo
   permita. Si una persona desempeña más de una, queda una justificación y un
   control alternativo auditables.
6. Las cantidades admiten decimales de acuerdo con la unidad de medida. Para
   cantidades, costos e importes se usa precisión decimal de base de datos y
   aplicación; no `float` ni cálculo desde el precio actual del producto.

## Modelo SC-2-04 — Informe de recepción

Las entradas por factura o conduce emiten un `SC-2-04` interno; el número de
factura o conduce es una referencia externa y no sustituye su consecutivo.
La cabecera conserva finca, almacén, fuente, proveedor/remitente, fecha,
responsables que entregan y reciben, estado `REGISTRADO` y el movimiento físico
enlazado. Cada línea congela producto, unidad de medida, cantidad, costo
unitario, importe y saldo posterior. El expediente no tiene rutas de edición o
borrado: un error se corrige mediante el documento compensatorio autorizado.
Su PDF es una reimpresión de solo lectura y no altera contabilidad ni stock.

## Modelo SC-2-06 — Entrega de productos terminados al almacén

### Uso

Formaliza la producción acabada que el área productora entrega al almacén, sea
para comercialización o para usarla posteriormente como insumo. La selección
de «Entrada por producción» debe crear este documento en la misma transacción
que incrementa el stock del almacén y el consolidado de la finca.

### Datos obligatorios

| Grupo | Datos que deben persistirse |
|---|---|
| Identificación | Tipo `SC-2-06`, número consecutivo, fecha y hora de emisión, finca/entidad y sus códigos. |
| Origen y destino | Área productora y código; almacén receptor y código/inventario; finca asociada. |
| Trazabilidad productiva | Número de orden de producción y/o lote. No se sustituye por un texto libre de observaciones. |
| Por renglón | Código y descripción del producto, unidad de medida, cantidad entregada por producción, cantidad recibida por almacén, costo real unitario, importe de la cantidad recibida y saldo resultante en el almacén. |
| Totales | Total físico e importe total del documento. |
| Responsables | Entregado por el área productora, recibido por almacén, contabilizado y anotado en control de inventario; usuario, fecha/hora de cada confirmación y nombre impreso. |
| Control | Estado, observaciones, documento origen si existe, huella/versión del PDF y vínculos a los movimientos de stock. |

### Reglas específicas

- La cantidad recibida puede diferir de la entregada. Si difiere, el sistema
  exige motivo y deja el documento en revisión hasta aprobación; el stock se
  incrementa únicamente por la cantidad recibida autorizada.
- El costo unitario es el costo real definido al cierre de la producción; no se
  toma del precio de venta ni se recalcula al consultar el PDF.
- Cada línea almacena el saldo anterior y el saldo posterior del almacén. El
  saldo posterior del documento es el que aparece en su PDF, incluso años
  después.
- Un producto solo puede recibirse en un almacén activo de la misma finca y
  previamente asignado a este.
- No se puede sustituir el producto, almacén, cantidad recibida ni costo luego
  de emitido. Un error se resuelve mediante reversión SC-2-06 o ajuste aprobado
  que señale el número original.

## Modelo SC-2-08 — Vale de entrega o devolución

### Uso

Ampara el despacho desde un almacén para consumo, centro de costo, trabajador,
comedor, insumo u otro receptor, o la devolución al almacén distribuidor. Una
salida múltiple genera **un solo vale** con varios renglones, no varios vales.

### Datos obligatorios

| Grupo | Datos que deben persistirse |
|---|---|
| Identificación | Tipo `SC-2-08`, consecutivo, fecha/hora, entidad y código. |
| Emisor y cargo | Almacén emisor; área, centro de costo o producto que recibe el cargo/abono; número de lote, orden de producción o de trabajo cuando aplique. |
| Receptor | Nombre, código y, si corresponde, dirección o trabajador/receptor detallado. En vales a trabajadores, cada producto conserva la relación de trabajadores y cantidades. |
| Por renglón | Código, descripción, UM, cantidad despachada o devuelta, costo/precio unitario autorizado, importe, saldo anterior y saldo posterior del almacén. |
| Totales | Total de unidades e importe. |
| Firmas/roles | Entrega, recepción, anotación en control de inventario y contabilización, con usuario y fecha/hora digitales. |
| Control | Estado, motivo/observaciones, documento origen, referencias de pagos cuando exista venta y vínculo a todos los movimientos de stock. |

### Reglas específicas

- El sistema bloquea el despacho si cualquier renglón deja el saldo físico del
  almacén por debajo de cero. La validación y el descuento ocurren en la misma
  transacción.
- El costo/precio y el saldo de cada renglón quedan congelados al emitir. No se
  obtienen del catálogo ni del stock actual para reimpresiones.
- Una devolución referencia el vale que devuelve, invierte sus cantidades de
  manera trazable y conserva un consecutivo propio.
- Cuando la operación sea una venta o implique tránsito a cliente, el vale se
  vincula con los documentos que corresponda: SC-2-10 Orden de despacho,
  SC-2-11 Conduce y/o factura. Ninguno sustituye al otro si su uso aplica.

## Modelo SC-2-09 — Transferencia entre almacenes

### Uso y flujo físico

Ampara productos que pasan entre almacenes de la misma entidad/finca o centros
de costo. Debe representar dos fases: despacho desde origen y recepción en
destino; no se considera disponible en destino antes de que este confirme.

### Datos obligatorios

- Número consecutivo, fecha/hora, entidad/código.
- Almacén que entrega y almacén receptor: nombre, código e identificación de
  inventario/dirección según corresponda.
- Por línea: código, descripción, UM, cantidad remitida, cantidad recibida,
  costo unitario, importe remitido, importe recibido, saldo de origen y saldo
  de destino resultantes.
- Totales de remisión y recepción.
- Entrega, recepción, autorización de transferencia, anotación de inventario
  de ambos almacenes y contabilización de ambos; usuarios y fechas digitales.

### Reglas específicas

- Al emitir el despacho se reduce el disponible del origen y el documento queda
  `EN_TRANSITO`; el destino no aumenta aún su disponible.
- Al confirmar recepción se ingresa solo lo realmente recibido. Una diferencia
  requiere motivo, revisión y documento de reclamación/ajuste según el manual
  interno. La cantidad pendiente sigue identificable hasta resolverse.
- La cancelación antes de recibir devuelve el stock al origen mediante una
  reversión auditada. Tras recepción, solo procede una transferencia inversa o
  ajuste autorizado.

## Estados y transiciones comunes

| Estado | Significado | Acciones permitidas |
|---|---|---|
| `BORRADOR` | Datos aún editables; no afecta existencias. | Editar, eliminar borrador, solicitar emisión. |
| `PENDIENTE_APROBACION` | Espera autorización cuando el flujo de la finca lo requiera. | Aprobar, rechazar a borrador. |
| `EMITIDO` | Documento autorizado y movimiento aplicado. | Descargar/reimprimir; confirmar recepción donde aplique; iniciar reversión. |
| `EN_TRANSITO` | Solo SC-2-09: origen despachó y destino aún no recibió. | Confirmar recepción, registrar diferencia, reversar antes de recepción. |
| `RECIBIDO` | SC-2-06 recibido o SC-2-09 confirmado por destino. | Descargar; iniciar documento de corrección. |
| `RECHAZADO` | No autorizado; no afecta existencias. | Corregir y volver a solicitar o archivar. |
| `ANULADO` | Documento conservado, sin vigencia; debe apuntar a su reversión. | Solo consulta y PDF marcado como anulado. |
| `REVERSADO` | Efecto de stock compensado por documento sucesor. | Solo consulta y PDF. |

Las transiciones se registran en un historial con estado anterior/nuevo, motivo,
usuario, rol y fecha/hora. `EMITIDO`, `RECIBIDO`, `ANULADO` y `REVERSADO` son
inmutables en su contenido económico y físico.

## Snapshots y trazabilidad mínima de datos

Para cada línea documental se conservarán, además de sus referencias UUID:

- Código, nombre y UM que se mostraron al emitir.
- Cantidad solicitada, entregada/remitida y recibida, según el modelo.
- Costo real/precio unitario, importe y moneda.
- Saldo anterior y posterior del almacén afectado; en transferencia, de ambos
  almacenes al momento de cada fase.
- Identidad de almacén, finca, área/centro de costo, orden y lote.
- Vínculo uno a uno o uno a muchos con `movimiento_stock`, incluido tipo,
  referencia y fecha efectiva.
- Autor, aprobador, despachador, receptor, anotador de inventario y
  contabilizador cuando aplique.
- Datos de emisión y versión/huella del PDF.

El PDF se genera exclusivamente a partir de esos snapshots. Si se muestra
información actual como consulta adicional, debe rotularse como «información
actual» y nunca reemplazar la evidencia original.

## Inventario, submayor, tarjeta de estiba y ajustes

1. El submayor SC-2-13 debe permitir consultar por almacén y producto:
   ubicación, cuenta/subcuenta/análisis, fecha, documento origen, entradas,
   salidas, existencia, unidades e importes/costo promedio cuando aplique.
2. La tarjeta de estiba SC-2-14 controla unidades: producto, almacén, fecha,
   documento origen, unidades recibidas, entregadas, saldo después de cada
   operación y responsable que anota.
3. Inventario físico SC-2-15 compara conteo y submayor, muestra faltantes o
   sobrantes físicos e importes; requiere quien realiza y quien comprueba.
4. Ajuste SC-2-16 solo se emite con concepto/motivo, cantidades, costo,
   importes y saldo, total, jefe de almacén, inventariador, contabilidad y
   control de inventario. No se usa una edición de documento emitido para
   reemplazar un ajuste.

## Seguridad y auditoría

- Autorización por rol y finca/tenant para crear, aprobar, despachar, recibir,
  anular/reversar, contabilizar y descargar documentos.
- El sistema registra usuario autenticado, fecha/hora y dirección/identificador
  de sesión para eventos relevantes. Las firmas impresas muestran el nombre y
  cargo asociado al usuario en ese momento.
- La anulación/reversión exige motivo no vacío y referencia explícita al
  documento afectado. Se prohíbe el borrado físico de cualquier documento
  emitido y de sus líneas/movimientos asociados.
- Las operaciones que actualizan stock usan transacción y control de
  concurrencia para impedir doble emisión o saldos negativos.

## Despliegue en las otras PC

1. Implementar primero backend, migración Flyway nueva y pruebas. Nunca editar
   una migración que ya se haya aplicado en alguna PC.
2. Versionar la migración con el siguiente número disponible y añadirla a
   `deploy/apply-db-schema.sh` si el actualizador local lo requiere.
3. Publicar backend, frontend y repositorio principal en GitHub. GitHub es la
   fuente de verdad para código, pero **no** para datos de PostgreSQL.
4. Cuando no haya usuarios trabajando, ejecutar en cada PC:

   ```bash
   ~/sistema-finca/update-from-github.sh
   ```

   El actualizador descarga `main`, compila, aplica las migraciones pendientes
   y reinicia los servicios. No ejecutar SQL manual ni reemplazar la base de
   datos desde otra PC.
5. Después de actualizar, confirmar salud y comprobar que se crearon las
   nuevas tablas, restricciones, índices y consecutivos con una entrada y una
   salida de prueba en una finca de ensayo.

## Checklist operativo de aceptación

### Producción terminada

- [ ] La entrada «Producción» obliga área productora, almacén receptor, orden
      o lote, cantidades entregada/recibida, costo real y responsables.
- [ ] La confirmación crea exactamente un SC-2-06 y los movimientos de stock
      correspondientes, sin duplicar el consolidado de finca.
- [ ] El PDF presenta consecutivo persistente, datos obligatorios y firmas.
- [ ] Cambiar el precio, producto o stock después no modifica el PDF histórico.
- [ ] Una corrección/anulación genera una reversión vinculada y no borra datos.

### Salidas y vales

- [ ] Una salida múltiple crea un solo SC-2-08 con todas sus líneas y, cuando
      aplique, la distribución por trabajador.
- [ ] El vale incluye almacén, área/centro de costo, receptor, orden/lote,
      cantidades, costo/precio, importes, saldos, total, firmas y consecutivo.
- [ ] No se permite saldo negativo ni descuento doble ante solicitudes
      simultáneas.
- [ ] Devoluciones y anulaciones conservan el vale original y el enlace de
      reversión.

### Transferencias y control posterior

- [ ] La salida de origen queda `EN_TRANSITO`; el destino solo aumenta con su
      confirmación de recepción.
- [ ] Las diferencias de remisión/recepción se justifican y siguen visibles
      hasta su solución.
- [ ] Submayor y tarjeta de estiba muestran cada operación con su documento y
      saldo histórico correcto.
- [ ] Inventario físico y ajustes mantienen firmas/responsables, motivo y
      trazabilidad contable.
