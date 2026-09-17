---
document: transferencias-sc209
status: implemented
model: SC-2-09
migration: V29__transferencias_almacen_sc209.sql
---

# Transferencia entre almacenes — SC-2-09

Una transferencia nueva se ejecuta en dos actos trazables:

1. **Despacho.** `POST /api/almacen/{almacenOrigenId}/transferir` verifica finca,
   almacenes activos, motivo y saldo disponible. Emite un consecutivo
   `SC2-09-AÑO-#####`, descuenta únicamente el almacén origen y deja la cabecera
   y sus renglones en estado `EN_TRANSITO`.
2. **Recepción.** El almacén destino consulta
   `GET /api/almacen/{almacenDestinoId}/transferencias-pendientes` y confirma
   físicamente cada renglón con `POST .../transferencias/{id}/recibir`. Solo las
   cantidades aceptadas incrementan el destino. Una diferencia se exige con
   explicación y se reintegra automáticamente al origen; el documento termina
   `RECIBIDA` o `RECHAZADA`.

Antes de la recepción, el producto no está disponible en el destino. El origen
puede revertir el documento que siga en tránsito mediante `POST .../revertir`,
con motivo obligatorio; la reversión también reintegra la existencia y conserva
los movimientos previos. Las transferencias históricas no se convierten ni se
eliminan: siguen visibles como movimientos anteriores a V29.

La cabecera, renglones, cada movimiento físico y los cambios de estado generan
auditoría posterior al `commit`, con finca, número, usuario, cantidades, estado
anterior/nuevo y motivo. El consecutivo de SC-2-09 se consulta además junto a
los documentos oficiales de la finca.

## Prueba de aceptación

- Despachar 1.5 unidades: origen disminuye 1.5 y destino no varía.
- Recibir 1.0 y rechazar 0.5 con motivo: destino aumenta 1.0 y origen recupera
  0.5; no cambia el total de la finca.
- Revertir un documento en tránsito: el origen recupera todo y no se permite
  una posterior recepción.
- Reintentar recibir/revertir un documento cerrado: la API debe responder con
  conflicto de estado, sin alterar saldos.
