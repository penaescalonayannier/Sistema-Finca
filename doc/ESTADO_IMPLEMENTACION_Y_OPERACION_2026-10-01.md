# Estado de implementación y operación

Fecha de consolidación: 2026-10-01.

Este documento reúne los cambios funcionales, técnicos y operativos realizados en Sistema Finca. Complementa las especificaciones de `contabilidad/specs/`; no reemplaza sus contratos ni la documentación específica de cada módulo.

## Arquitectura y repositorios

| Componente | Ubicación | Tecnología |
| --- | --- | --- |
| Backend | `contabilidad/` | Spring Boot 3, Java 21, PostgreSQL, Redis |
| Frontend | `mi-proyecto-vue/mi-proyecto/` | Vue 3 y TypeScript |
| Librería compartida | `share/` | Java |
| Migraciones | `contabilidad/src/main/resources/db/migration/` | SQL incremental |
| Despliegue | `deploy/` | Bash, PostgreSQL y systemd |

El repositorio principal mantiene el frontend como repositorio separado. En una actualización deben sincronizarse ambos repositorios en la rama `main`.

## Inventario, finca y almacenes

Se completaron y reforzaron los siguientes flujos:

- Gestión de productos por finca con existencias decimales, incluidos valores como `1.5`.
- Ajuste contable de existencias por finca-producto, indicando el almacén afectado y registrando el movimiento correspondiente.
- Entradas, salidas y transferencias de almacén con cantidades decimales y actualización coherente de inventario físico, finca-producto y movimientos.
- Salida múltiple: varios productos se registran en un único vale, en lugar de generar un vale por cada producto.
- Salida a trabajadores por producto: cada producto puede tener su propia relación de trabajadores compradores y el vale detalla quién recibió cada artículo.
- Validaciones de producto, stock, almacén y consistencia de las relaciones `Salida` / `ItemSalida`.
- Generación de tarjeta de estiba por almacén y producto para un período; incluye entradas, salidas, transferencias, saldo y origen del movimiento.
- Tarjeta de estiba en Gestión de Productos por Finca, consolidando los movimientos de todos los almacenes de la finca e identificando el almacén originador.
- Entradas por tipo **Producción** vinculadas al documento de Producción Terminada.
- Producción Terminada con documento descargable en PDF y datos de inventario/almacén persistidos en el documento.
- Revisión de todos los puntos de entrada y salida para evitar mutaciones de stock sin trazabilidad.

La documentación de referencia está en `contabilidad/specs/features/almacen/`, `finca-producto/`, `movimiento-stock/` y `produccion-terminada/`.

## Vales de salida, facturas y reportes de producción

- PDF individual de vale de salida con diseño mejorado: suministrador y receptor en dos columnas y etiquetas en negrita.
- Exportación de vales de un día en un único PDF, con modalidades de agrupación por destino y de vales individuales por página cuando no caben dos en una hoja.
- Agrupación visual de vales por destino para ahorrar papel sin alterar contabilidad ni stock.
- Reporte de destinos de producción según las salidas, con descarga PDF.
- Búsqueda y control de salidas relacionadas con facturas.
- Reporte Consolidado de Movimientos con identificación de efectivo generado y los vales/facturas que le dieron origen.

## Caja, efectivo y banco

El módulo de Caja incorporó control documental, trazabilidad y operaciones de efectivo:

- Relación de facturas y vales cobrados en efectivo, incluyendo vales de trabajadores con pagos mixtos: efectivo y transferencia.
- Entrega de documentos a Caja: la contadora selecciona comprobantes, los entrega a caja y quedan marcados para no volver a estar disponibles.
- Entrada de caja por denominaciones de billetes: 5, 10, 20, 50, 100, 200, 500, 1 000, 2 000, 5 000, 10 000 y 20 000.
- Salida o depósito al banco usando las mismas denominaciones, con listado de depósitos.
- Cambio de denominaciones de efectivo y entrega de cambio cuando un pago supera el importe del documento.
- Arqueo sorpresivo: selección parcial o total, conteo por denominación, observaciones, historial, detalle y PDF.
- Entrega de efectivo al banco para descargar el saldo físico de caja.
- Auditoría de movimientos de caja, responsable, fecha y documento origen.

Las reglas de formas, caja y banco están documentadas en:

- `contabilidad/specs/features/caja/`
- `contabilidad/specs/MAPA_FORMAS_NUMERADAS_CAJA_BANCO.md`
- `contabilidad/specs/REGISTRO_FORMAS_NUMERADAS_RECOMENDACION.md`

## Contabilidad y formas numeradas

- Se corrigió la generación de UUID en salidas nuevas.
- Se corrigió el modelo de `ItemSalida` para la salida múltiple y se añadieron las columnas requeridas mediante migración.
- Se resolvieron rutas de controladores que interpretaban segmentos estáticos como UUID, por ejemplo `vales` como parámetro `id`.
- Se evitaron errores de inicialización diferida (`LazyInitializationException`) en consultas de reportes y otros endpoints de lectura mediante cargas/DTOs apropiados.
- Se corrigieron los consecutivos de asientos contables para evitar duplicados del tipo `AS-YYYYMMDD-####`.
- Existe registro de formas numeradas, series por finca/ámbito/período, emisión auditable y vistas para consultar sus valores.
- Facturas, vales, producciones terminadas y documentos de Caja/Banco disponen de consecutivos por tipo de forma, conservando los históricos.

## Planificación de ingresos por ventas

- Nueva vista en **Finanzas → Plan de Ingresos por Ventas**.
- Cada plan pertenece a una finca y un mes; solo puede existir un borrador por esa combinación.
- Los renglones seleccionan productos activos y guardan una fotografía de código, nombre, unidad, tipo de precio, cantidad y precio unitario. No quedan enlazados al producto una vez guardados.
- La vista permite elegir precio general, de trabajador o de comedor y calcula en pantalla ingresos, pago salarial, aporte al presupuesto, compras, servicios, egresos y disponible.
- El backend vuelve a calcular todos los importes con precisión decimal y registra auditoría. No se producen movimientos de inventario, facturación, caja ni contabilidad.

## Recursos Humanos y reportes de trabajo

### Recursos Humanos

- Estructura organizativa por finca: áreas de trabajo, grupos y plazas.
- Cargos, salarios e historial salarial.
- Disciplina laboral: expedientes, estados, medidas, evidencia, notificación, resolución y anulación.
- Evaluaciones de desempeño y ciclo de cierre.
- Campo **Nivel Cultural** en Trabajador, disponible en entidad, API, creación, edición, listados, detalle y PDF.
- Reporte PDF desde el listado de trabajadores con nombre, RUC y nivel cultural.
- Migración de normalización que asigna `12 grado` a los trabajadores históricos.

### Gestión de Reportes / Vista Excel

- Edición de horas y norma por celda.
- Alta y baja de días y trabajadores por reporte.
- Acciones masivas L-V de 8 h y 4 h, sábados de 4 h y domingos de 4 h.
- Las acciones masivas crean las filas faltantes cuando se agrega un día nuevo a un reporte ya existente.
- Botón visible `⏱` en cada columna para definir horas del día.
- El modal de columna permite seleccionar trabajadores opcionalmente:
  - sin selección aplica a todos;
  - con una o más selecciones aplica solo a esos trabajadores.
- Botón visible `⏱` en cada fila para definir las horas de un trabajador en todos los días del reporte, creando celdas faltantes y preservando la norma existente.

El comportamiento de frontend fue validado con `npm run type-check` y compilación de producción. La vista puede requerir `Ctrl + F5` tras actualizar el frontend en desarrollo.

## Migraciones relevantes

Las migraciones son aditivas y deben ejecutarse en orden. No se deben editar migraciones ya aplicadas.

| Migración | Propósito |
| --- | --- |
| V16 | Soporte de producto y almacén en `item_salida` para salidas múltiples |
| V17-V19 | Existencias, movimientos y producción con precisión decimal |
| V20-V29 | Caja, liquidaciones, banco, arqueos, documentos y auditoría |
| V30-V37 | Recepción, conteo físico, tenant de reportes, estructura organizativa, disciplina, evaluación, salario y consecutivos contables |
| V38-V43 | Registro de formas numeradas, series, documentos Caja/Banco y unicidad por finca |
| V44 | Columna `nivel_cultural` en `trabajador` |
| V45 | Inicializa el nivel cultural histórico en `12 grado` |
| V46 | Plan mensual de ingresos por ventas con detalles históricos por producto |

`deploy/apply-db-schema.sh` incluye V44, V45 y V46. La guía de ejecución está actualizada en `deploy/ACTUALIZACION-DOS-PC.md`.

V46 no altera existencias, movimientos, facturas, caja ni asientos contables. Cada elemento del plan persiste el código, nombre, unidad y precio como una instantánea para conservar el valor histórico aun si el catálogo de productos cambia después.

## Despliegue en PC de Contabilidad y RRHH

La fuente de verdad es GitHub. Las estaciones deben actualizar desde `main`, no copiando cambios manuales entre computadoras.

Procedimiento operativo recomendado:

1. Comprobar que no haya usuarios realizando operaciones críticas.
2. Respaldar o preservar archivos locales de configuración:
   - backend: `application.properties` y `application-dev.properties`;
   - frontend: `.env`, `.env.development` y `.env.production`.
3. Actualizar el repositorio principal y el repositorio del frontend a `origin/main`.
4. Compilar `share` e instalarlo en Maven local.
5. Compilar el backend y copiar el JAR de forma atómica.
6. Ejecutar `deploy/apply-db-schema.sh` con las propiedades locales.
7. Ejecutar `npm ci --legacy-peer-deps` en frontend.
8. Reiniciar mediante systemd los servicios `sistema-finca-backend.service` y `sistema-finca-frontend.service`.
9. Comprobar `http://127.0.0.1:9908/actuator/health` hasta obtener `{"status":"UP"}`.

El backend puede tardar alrededor de uno o dos minutos en iniciar en las PC remotas. No debe diagnosticarse como caído inmediatamente después del reinicio.

### Estado verificado de RRHH

- Se instaló el código actualizado y se compilaron backend y frontend.
- Se aplicaron las migraciones, incluida V45 para 125 trabajadores históricos.
- Los servicios de backend y frontend están habilitados en systemd.
- En la última comprobación, el backend respondió `UP` y el frontend respondió HTTP 200.

## Plantilla de prenómina en RRHH

El servicio de prenómina intenta cargar un archivo Excel `.xls` y no puede usar la ruta absoluta de desarrollo de otra computadora.

En RRHH se instaló la plantilla desde el backend local en una ruta persistente:

```text
/home/pipe/sistema-finca/config/prenomina/
PRENOMINA SALARIO BANDEC FINCA LORENZO DALIS MANATI.xls
```

La configuración local de RRHH contiene:

```properties
prenomina.excel.path=/home/pipe/sistema-finca/config/prenomina
prenomina.excel.filename=PRENOMINA SALARIO BANDEC FINCA LORENZO DALIS MANATI.xls
```

Esta ubicación está fuera de los repositorios y se conserva durante actualizaciones. Para instalar otra PC se debe copiar la misma plantilla a su directorio de configuración y definir las propiedades equivalentes; no se deben documentar ni usar rutas personales de desarrollo.

## Validaciones técnicas realizadas

- Compilación Java del backend y pruebas unitarias específicas cuando se modificaron handlers de finca-producto/trabajador.
- `npm run type-check` y `npm run build` para las modificaciones de interfaz de RRHH.
- Comprobación de `git diff --check` antes de commits.
- Verificación de migraciones en PostgreSQL local y en RRHH.
- Verificación de servicios remotos mediante `systemctl`, endpoint de salud y respuesta HTTP del frontend.

## Advertencias y mantenimiento pendiente

- Las advertencias de ESLint y de tamaño de bundle del frontend no bloquean la compilación, pero conviene planificar su reducción.
- La plantilla de prenómina es un archivo operativo externo: debe incluirse en la lista de preparación de cualquier nueva estación.
- Los mensajes por JWT expirado en logs indican sesiones vencidas del navegador; no fueron la causa del fallo de prenómina. El usuario debe iniciar sesión de nuevo si aparecen.
- Antes de aplicar cambios en producción, evitar operaciones manuales de borrado de documentos, stock o contabilidad sin respaldo y trazabilidad.
- Cualquier migración nueva debe añadirse al script de despliegue y a `deploy/ACTUALIZACION-DOS-PC.md` en el mismo cambio.

## Referencias internas

- [Guía de actualización de dos PC](../deploy/ACTUALIZACION-DOS-PC.md)
- [Documentación de despliegue](../DEPLOY.md)
- [Operación de actualización desde GitHub](../OPERACION_ACTUALIZACION_GITHUB.md)
- [Auditoría de almacenes](../contabilidad/specs/features/almacen/AUDITORIA_CUMPLIMIENTO_CUBANO_ALMACENES_2026-09-16.md)
- [Auditoría de caja](../contabilidad/specs/features/caja/AUDITORIA_CUMPLIMIENTO_CUBANO_CAJA_2026-09-16.md)
- [Trabajadores](../contabilidad/specs/features/trabajador/SPEC.md)
