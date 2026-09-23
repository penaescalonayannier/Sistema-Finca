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
5. Ejecuta con `psql` las migraciones de esquema antes de iniciar el sistema, incluidas `V20`–`V37` y `V38__registro_formas_numeradas.sql`, `V39__semillas_formas_numeradas.sql`, `V40__numeros_documentales_liquidacion_y_entrega_banco.sql`, `V41__inicializar_series_formas_desde_consecutivos.sql`, `V42__formas_numeradas_caja_banco_control.sql`, `V43__unicidad_formas_por_finca.sql`, `V44__nivel_cultural_trabajador.sql` y `V45__establecer_nivel_cultural_12_grado.sql`.
6. Reinicia los servicios y consulta `http://127.0.0.1:9908/actuator/health`.

Las migraciones nuevas son aditivas y no renumeran documentos históricos. V38 crea el catálogo, series y libro auditable de formas; V39 registra las formas iniciales; V40 da consecutivo visible a la entrega de documentos a Caja y a la entrega al Banco; V41 inicia las series desde los máximos históricos; y V42 incorpora el consecutivo visible de las formas de Caja y Banco. V17 y V18 dejan las existencias y cantidades de movimientos con precisión de cuatro decimales. Así, valores como `1.5` se conservan en productos de finca, almacenes, entradas, salidas, transferencias, vales y reportes.

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
