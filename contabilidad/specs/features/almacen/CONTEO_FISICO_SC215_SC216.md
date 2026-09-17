# Conteo físico de almacén — SC-2-15 / SC-2-16

El módulo crea un expediente por almacén (SC-2-15) con el saldo teórico
instantáneo, producto, unidad, responsables, fecha y observaciones. No modifica
existencias durante su apertura. Al cierre es obligatorio declarar cada línea,
el responsable que autoriza y las observaciones necesarias.

Si hubo movimientos desde la apertura, el sistema rechaza el cierre: el
expediente debe rehacerse, para no usar un saldo teórico vencido. Las diferencias
solo se convierten en movimientos `ENTRADA_AJUSTE` o `SALIDA_AJUSTE` al cierre,
quedan referidas al expediente y reciben consecutivo SC-2-16. Un conteo sin
diferencias se cierra sin documento de ajuste.

La apertura y el cierre se registran en Auditoría. El PDF conserva ambos
documentos y no es un mecanismo para modificar el stock.
