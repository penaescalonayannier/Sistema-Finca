# Operación: GitHub como fuente de verdad

## Alcance

GitHub es la fuente de verdad para el **código** de Sistema Finca. Los datos operativos no se obtienen desde GitHub: cada PC conserva su PostgreSQL, Redis y los archivos locales de configuración.

| Componente | Repositorio y rama usados en el despliegue |
|---|---|
| Backend y librería `share` | `penaescalonayannier/Sistema-Finca`, `main` |
| Frontend | `penaescalonayannier/finca-frontend`, `main` |

El repositorio `Finca-Backend` también se mantiene publicado, pero el actualizador usa `Sistema-Finca` para construir el backend porque allí se encuentra la librería `share` necesaria para la compilación.

## Regla de seguridad

Una actualización de código **no debe** restaurar, copiar ni limpiar bases de datos. Tampoco se versionan ni se sobrescriben los siguientes archivos locales:

- `~/sistema-finca/backend/application.properties`
- `~/sistema-finca/backend/application-dev.properties`
- `~/sistema-finca/frontend/.env*`

El actualizador guarda las copias protegidas en `~/sistema-finca/config/` antes de reemplazar código. La primera vez que se ejecuta también resguarda el frontend anterior como `~/sistema-finca/frontend.legacy-AAAAMMDD-HHMMSS`.

## Instalación inicial por PC

La preparación se realiza una sola vez y no reinicia servicios:

```bash
sudo apt-get update
sudo apt-get install -y git
mkdir -p ~/sistema-finca/source
git clone --branch main --single-branch \
  https://github.com/penaescalonayannier/Sistema-Finca.git \
  ~/sistema-finca/source/Sistema-Finca
install -m 755 \
  ~/sistema-finca/source/Sistema-Finca/deploy/update-from-github.sh \
  ~/sistema-finca/update-from-github.sh
```

Java 21, Node 20, npm y `curl` deben estar instalados. Maven global no es necesario: el script usa el Maven Wrapper del backend para instalar `share` y compilar el JAR.

## Actualización normal

Solo se ejecuta cuando el usuario confirme que no hay personal trabajando con el sistema, porque reinicia backend y frontend:

```bash
~/sistema-finca/update-from-github.sh
```

El proceso descarga `main`, compila la librería compartida y el backend, ejecuta `npm ci`, aplica las migraciones de esquema incluidas en la versión, sustituye el JAR, reinicia los servicios y consulta `http://127.0.0.1:9908/actuator/health`.
El propio actualizador se renueva desde GitHub para la siguiente ejecución.
El frontend se instala con `npm ci --legacy-peer-deps` debido a la incompatibilidad conocida entre sus versiones fijadas de ESLint y el validador de pares de npm 10.

Para verificar después de una actualización:

```bash
curl --fail http://127.0.0.1:9908/actuator/health
git -C ~/sistema-finca/source/Sistema-Finca log -1 --oneline
git -C ~/sistema-finca/frontend log -1 --oneline
```

Si una compilación falla antes del reinicio, los servicios actuales siguen ejecutándose. No se debe usar `git pull` manualmente dentro de los directorios gestionados: el actualizador los fija exactamente a `origin/main`.

## Módulo de liquidación, caja y banco (versión 2026-09-16)

La versión actual incorpora el control trazable de cobro de vales y facturas. La operación se realiza en la nueva vista **Liquidación / Entrega a caja** y no modifica inventario: solo registra el cobro y su efecto sobre caja/deuda.

1. Seleccione finca y período. La lista presenta únicamente renglones con saldo pendiente.
2. Seleccione uno o más renglones y distribuya el importe entre **efectivo** y **transferencia**. Se permiten cobros parciales y pagos mixtos. Una transferencia exige su referencia bancaria.
3. Al registrar, cada aplicación queda vinculada al ítem, al vale o factura y a la forma de pago. Los renglones totalmente saldados dejan de aparecer como pendientes. Para trabajadores, se reduce la deuda por el importe aplicado.
4. Solo el efectivo incrementa el saldo de caja. Las transferencias no pasan por caja física.
5. En **Entrega de efectivo al banco**, registre el depósito con finca, importe, fecha y referencia bancaria. El importe debe ser menor o igual al saldo disponible; el depósito resta caja pero no borra ningún cobro ni documento.

El **Reporte Consolidado de Movimientos** y su PDF incluyen las filas de efectivo confirmado: liquidación, vale/factura, destino, trabajador, importe y estado del documento. Los documentos emitidos sin aplicación de pago no se contabilizan como efectivo.

### Migración requerida

Las migraciones Flyway `V20__liquidacion_salida_y_caja.sql` y `V21__control_caja_denominaciones.sql` se aplican automáticamente al ejecutar el actualizador. V21 registra el arqueo físico con las denominaciones CUP de 5, 10, 20, 50, 100, 200, 500, 1 000, 2 000, 5 000, 10 000 y 20 000. No debe ejecutarse SQL manual ni editarse una migración ya aplicada.

Después de actualizar una PC, compruebe:

```bash
curl --fail http://127.0.0.1:9908/actuator/health
```

- Abra **Liquidación / Entrega a caja** y valide que aparecen los pendientes de una finca.
- Compruebe el saldo de caja antes de registrar una operación real.
- Tras una liquidación real, confirme que el PDF consolidado muestra el vale/factura y que el saldo pendiente disminuyó exactamente.
- Antes de una entrega al banco, verifique el importe disponible; el sistema rechazará depósitos mayores al saldo de caja.

Los documentos históricos marcados como pagados sin una forma de pago trazable se conservan como históricos y no se incorporan artificialmente a caja. A partir de esta versión, los cobros nuevos quedan vinculados al documento de origen.

### Arqueo inicial de billetes

Si la caja tenía efectivo antes de V21, la vista mostrará el valor `pendienteSinDesglose`. Antes de realizar un depósito con billetes debe pulsarse **Registrar apertura física** e indicar el conteo real. La suma de los billetes debe coincidir exactamente con ese importe. A partir de entonces, todo cobro en efectivo y toda entrega al banco exige su desglose; el sistema no permite retirar más billetes de los existentes.

## Estado registrado el 2026-09-16

| PC | Estado | Observaciones |
|---|---|---|
| Contabilidad (`192.168.1.143`) | Preparada | Git instalado; `Sistema-Finca` clonado y `~/sistema-finca/update-from-github.sh` instalado. La primera actualización no se ha ejecutado, por lo que no se reiniciaron servicios ni se modificó la BD. |
| RRHH (`192.168.1.245`) | Preparada | Git instalado; `Sistema-Finca` clonado en el commit `f4cb8ab` y `~/sistema-finca/update-from-github.sh` instalado. La primera actualización no se ha ejecutado, por lo que no se reiniciaron servicios ni se modificó la BD. Se observaron dos procesos existentes de frontend; no fueron iniciados ni detenidos durante esta preparación. |
| Central | Publicado | El actualizador se encuentra versionado en `deploy/update-from-github.sh`. |

## Responsabilidad operativa

Antes de publicar una nueva versión, se valida localmente y se sube a GitHub. Después, cada PC descarga exclusivamente desde GitHub mediante este procedimiento. Las incidencias de compilación, credenciales de GitHub o conflictos de datos se registran y se resuelven antes de reintentar una actualización.
