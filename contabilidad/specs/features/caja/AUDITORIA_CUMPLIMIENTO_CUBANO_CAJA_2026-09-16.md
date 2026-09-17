# Auditoría de cumplimiento: Caja y Banco

Fecha: 2026-09-16. Alcance: liquidación de vales/facturas, caja física,
denominaciones, entrega al banco, arqueos, fondos/custodia, documentos de caja,
cheques, transferencias y conciliación.

## Referentes aplicados

- Resolución MFP 12/2007, Subsistema de Caja y Banco: modelos SC-3-01 a
  SC-3-08. En particular: cobros con recibo consecutivo y firmantes;
  vales de pagos menores autorizados; arqueo con efectivo, documentos del
  fondo, diferencia y conformidad del custodio; y conciliación bancaria
  mensual.
  [Texto consultado](https://www.cibercuba.com/s/gacetaoficial/resolucion-12-de-2007-de-ministerio-de-finanzas-y-precios).
- Resolución CGR 60/2011: legalidad, división de funciones, fijación de
  responsabilidades, cargo y descargo, autocontrol, información fiable y
  supervisión. [Texto consultado](https://www.cibercuba.com/s/gacetaoficial/resolucion-60-de-2011-de-contraloria-general-de-la-republica).

## Matriz de control

| Control | Estado | Evidencia y corrección |
|---|---|---|
| Cobro en efectivo y pago mixto | Implementado | Se liquida por renglón de salida; efectivo entra por denominaciones y transferencia exige referencia bancaria. El vuelto queda como movimiento independiente. |
| Saldo físico no negativo | Implementado | Las salidas, entregas y cambios bloquean el saldo de cada denominación antes de registrarse. |
| Entrega de efectivo al banco | Implementado | `EntregaBanco` y su movimiento enlazado conservan referencia, entrega/recibe, desglose de billetes y no permiten sobrepasar caja. |
| Cambio de billetes | Implementado | Un movimiento de importe cero conserva el saldo total y registra denominaciones entregadas/recibidas. |
| Arqueo SC-3-06 | Parcial controlado | Congela el esperado, detalle por denominación, conteo, diferencia, observación obligatoria si difiere, custodio/contador, PDF y expediente. No modifica caja. |
| Fondos y responsabilidad material | Implementado | Fondo autorizado, acta de responsabilidad, cierre y expediente por faltante/sobrante. |
| Documentos SC-3-01/03 | Parcial | Hay recibo, vale, anticipo, liquidación y reembolso con número, concepto, importe y campos de firmantes. Para no romper documentos existentes, el backend aún no exige todos los firmantes ni modela el importe en letras. Debe configurarse como procedimiento de emisión y cerrarse en una mejora posterior. |
| Anticipos a justificar SC-3-04 | Pendiente | Falta el control cronológico de vencimiento, importe usado/devuelto y bloqueo de nuevo anticipo a receptor moroso. No debe usarse el tipo `ANTICIPO` como sustituto de ese control. |
| Reembolso de fondos SC-3-05 | Pendiente | Falta relación estructurada de vales reembolsados, cuentas y cheque/transferencia de reembolso. |
| Arqueo completo SC-3-06 | Pendiente | El arqueo actual cubre efectivo CUP por denominación. Los documentos de valor, anticipos/vales no reembolsados y monedas deben incorporarse antes de declararlo arqueo integral de un fondo de pagos menores. |
| Control de cheques SC-3-07 | Parcial | Se registra emisión y confirmación; se debe completar entrega al beneficiario, fecha de cargo bancario, cancelación/caducidad y PDF/listado mensual. |
| Conciliación SC-3-08 | Parcial | Se guardan saldos y partidas; falta validación de saldo ajustado, identificación de cuenta bancaria y obligación de cierre mensual. |
| Segregación y acceso por finca | Implementado | Todos los puntos de Caja exigen validación de lectura/escritura por finca. La consulta de pendientes ya no admite una finca nula. |
| Bitácora | Implementado | Las operaciones materiales registran eventos posteriores al commit. V28 agrega además `usuario_id` y `created_at` a `movimiento_caja` y `entrega_banco`; conserva fecha económica sin reescribir historia. |

## Reglas operativas vigentes

1. La caja física solo se altera desde liquidación de efectivo, apertura por
   denominaciones, cambio de billetes, documento de caja que explícitamente la
   afecte, o entrega al banco.
2. El arqueo es una fotografía de control: una diferencia abre un expediente,
   nunca ajusta silenciosamente el saldo de caja.
3. Los eventos de auditoría se publican tras el `commit`; una transacción
   revertida no crea una evidencia falsa de movimiento.
4. V28 debe ejecutarse antes de desplegar el backend que escribe estos campos.
   El actualizador `deploy/apply-db-schema.sh` lo incluye.

## Pruebas de aceptación

- Liquidar un renglón efectivo con desglose; comprobar incremento de saldo,
  denominaciones y bitácora `MOVIMIENTO_CAJA`.
- Liquidar transferencia: debe exigir referencia y no aumentar caja.
- Intentar una entrega al banco por encima del saldo o sin los billetes
  disponibles: debe rechazarse sin crear movimiento.
- Hacer cambio 200 CUP por dos 100 CUP: saldo total igual y auditoría del
  cambio.
- Cerrar un arqueo con diferencia sin observación: debe rechazarse; con
  observación, debe crear el arqueo cerrado y permitir expediente.
- Confirmar en la base que las nuevas filas de `movimiento_caja` y
  `entrega_banco` tienen `created_at` y, bajo sesión autenticada, `usuario_id`.
