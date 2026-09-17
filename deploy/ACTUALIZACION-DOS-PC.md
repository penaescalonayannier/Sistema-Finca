# Actualización de las PC de trabajo

Este procedimiento aplica la misma versión de Sistema Finca en cada PC. GitHub es la fuente de verdad del código; los datos y la configuración de cada PC se mantienen locales.

## Antes de comenzar

1. Avise a los usuarios y espere a que terminen las operaciones en curso.
2. Confirme que la PC tiene Java 21, Node.js con npm, Git y `psql` disponibles.
3. Confirme que existen `~/sistema-finca/start-all.sh` y, si se usa para detener servicios, `~/sistema-finca/stop-all.sh`.
4. Como medida operativa recomendada, haga una copia de seguridad de la base de datos local antes de actualizar.

Puede comprobar las herramientas con:

```bash
java -version
node --version
npm --version
git --version
psql --version
```

## Actualizar una PC

Ejecute únicamente este comando desde una terminal:

```bash
~/sistema-finca/update-from-github.sh
```

El script realiza automáticamente, en este orden:

1. Descarga la rama `main` de los repositorios oficiales de backend y frontend.
2. Conserva fuera del código los archivos locales de conexión (`application*.properties`) y los `.env*` del frontend.
3. Compila `share`, el backend y el frontend.
4. Instala el JAR nuevo.
5. Ejecuta todas las migraciones Flyway pendientes antes de iniciar el sistema, incluidas `V20__liquidacion_salida_y_caja.sql`, `V21__control_caja_denominaciones.sql`, `V22__arqueos_sorpresivos_caja.sql`, `V23__caja_oficial_control.sql`, `V24__control_banco_y_documentos_caja.sql`, `V25__documento_caja_movimiento_efectivo.sql`, `V26__documento_produccion_snapshot.sql`, `V27__salida_finca_consecutivo_unico.sql`, `V28__auditoria_movimientos_caja.sql`, `V29__transferencias_almacen_sc209.sql`, `V30__informe_recepcion_sc204.sql`, `V31__conteo_fisico_almacen_sc215_sc216.sql`, `V32__reporte_finca_tenant.sql`, `V33__estructura_organizativa_y_plazas.sql`, `V34__expediente_disciplina_laboral.sql`, `V35__ciclo_formal_evaluacion_desempeno.sql`, `V36__historial_salarial_trabajador.sql` y `V37__consecutivo_asiento_contable_seguro.sql` cuando la PC aún no las tenga aplicadas.
6. Reinicia los servicios y consulta `http://127.0.0.1:9908/actuator/health`.

Las migraciones son idempotentes: se pueden ejecutar en ambas PC sin repetir datos ni alterar registros existentes. V17 y V18 dejan las existencias y cantidades de movimientos con precisión de cuatro decimales. V20 añade la trazabilidad de liquidación de vales/facturas, caja física y entregas al banco; V21 añade el arqueo por denominaciones; V22 incorpora las actas de arqueo sorpresivo de Caja; V23 añade fondos autorizados, actas de responsabilidad, incidencias y tablero de Caja; V24 añade documentos oficiales de Caja, cheques/transferencias y conciliación bancaria; V25 permite que un documento nuevo afecte Caja con desglose de billetes, sin duplicar liquidaciones existentes; V26 conserva el snapshot y consecutivo de Producción Terminada; V27 refuerza la finca y unicidad de consecutivos de vales/facturas; y V28 agrega usuario e instante técnico a los movimientos físicos de Caja y entregas al Banco, sin alterar sus importes ni fechas económicas. V32 añade la finca propietaria al parte de trabajo y la completa desde su responsable cuando existe, sin cambiar días, jornadas, códigos ni importes históricos. Para conservar la trazabilidad de los registros históricos, V28 completa solo `created_at` a partir de su fecha económica cuando faltaba; no recalcula importes, denominaciones ni saldos. Así, valores como `1.5` se conservan en productos de finca, almacenes, entradas, salidas, transferencias, vales y reportes.

## Después de actualizar

1. Espere el mensaje `Actualización completada` y compruebe que el health check no informa error.
2. Abra el sistema y valide una existencia conocida con decimales, por ejemplo `1.5`.
3. Registre una entrada o transferencia de prueba solamente si la operación lo permite; confirme que el valor no se redondea.
4. Repita exactamente el mismo comando en la segunda PC.
5. En **Finanzas**, valide las nuevas vistas **Fondos y Control de Caja**, **Documentos de Caja** y **Banco y Conciliación**. Registre primero los fondos/custodio de una finca y realice una prueba documental no operativa antes de utilizar los comprobantes reales.
6. En **Almacenes**, compruebe una entrada de producción y una salida múltiple de prueba. Las entradas de factura/conduce requieren ahora su número fuente; las operaciones físicas no se registran desde Gestión de Productos por Finca.
7. En **Recursos Humanos**, confirme que los trabajadores históricos abren sin plaza asignada. Compruebe las nuevas vistas **Estructura y plazas**, **Disciplina laboral**, **Evaluaciones** e **Historial salarial**. Las vigencias salariales son solo trazabilidad: no ejecutan nómina ni prenómina.

## Si falla

El actualizador se detiene antes de reiniciar si falta una herramienta, falla la compilación o no puede aplicar una migración. No edite el código dentro de `~/sistema-finca/source/Sistema-Finca` ni `~/sistema-finca/frontend`: el siguiente proceso restablece esas carpetas desde GitHub.

Revise el mensaje de error y conserve la configuración local en `~/sistema-finca/config/`. Si el fallo sucede durante la migración, no continúe usando la nueva versión hasta resolver la conexión o el esquema de esa PC.
