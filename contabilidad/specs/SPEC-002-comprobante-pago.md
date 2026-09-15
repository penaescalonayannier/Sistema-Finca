# SPEC-002: Comprobante de Pago de Deuda (PDF)

## Metadata
- **ID**: SPEC-002
- **Módulo**: Recibos/Comprobantes
- **Prioridad**: ALTA
- **Estado**: Propuesto
- **Fecha**: 2026-08-22

---

## 1. Problema

El sistema registra pagos de deuda pero no genera comprobantes:
- No hay PDF de recibo de pago
- No hay número de recibo/comprobante
- El trabajador no tiene documento que respalde su pago
- No hay registro imprimible para archivo físico

### Impacto
- Falta de respaldo documental
- Disputas de pago sin evidencia
- No cumple requisitos de auditoría

---

## 2. Requisitos Funcionales

### RF-001: Generación de Comprobante PDF
- Al registrar un pago, el sistema DEBE generar un PDF de comprobante
- El PDF DEBE ser descargable desde la UI
- DEBE poder regenerarse en cualquier momento

### RF-002: Contenido del Comprobante
El comprobante DEBE incluir:
- Número de recibo (formato: `REC-{AÑO}-{SECUENCIA}`)
- Fecha y hora del pago
- Datos del trabajador (nombre, RUC)
- Finca asociada
- Monto pagado
- Forma de pago (Efectivo/Transferencia)
- Referencia bancaria (si aplica)
- Saldo anterior
- Saldo nuevo (después del pago)
- Concepto/descripción
- Espacio para firma

### RF-003: Numeración de Recibos
- Cada recibo DEBE tener número único
- Formato: `REC-{AÑO}-{SECUENCIA:5 dígitos}`
- Ejemplo: `REC-2026-00001`
- Numeración independiente por finca

### RF-004: Historial de Pagos
- El sistema DEBE mostrar historial de pagos por trabajador
- Cada pago DEBE tener botón para descargar comprobante
- DEBE mostrar saldo acumulado

---

## 3. Requisitos No Funcionales

### RNF-001: Rendimiento
- La generación del PDF DEBE completarse en menos de 2 segundos

### RNF-002: Formato
- Tamaño: Media carta o A5 (ahorro de papel)
- Orientación: Vertical
- Resolución: Apta para impresión

---

## 4. Modelo de Datos

### Modificación a PagoDeuda

```java
@Entity
@Table(name = "pago_deuda")
public class PagoDeuda {
    // Campos existentes...

    @Column(name = "numero_recibo", unique = true)
    private String numeroRecibo;  // NUEVO

    @Column(name = "saldo_anterior")
    private Double saldoAnterior;  // NUEVO

    @Column(name = "saldo_nuevo")
    private Double saldoNuevo;  // NUEVO

    @Column(name = "concepto")
    private String concepto;  // NUEVO
}
```

### Reutilizar ConfiguracionNumeracion
- Agregar tipo: `RECIBO` al enum o usar tabla separada

---

## 5. Diseño del PDF

```
┌─────────────────────────────────────────────────────────┐
│                    COMPROBANTE DE PAGO                  │
│                                                         │
│  Finca: [Nombre de la Finca]                           │
│  No. Recibo: REC-2026-00001                            │
│  Fecha: 22/08/2026 14:35                               │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  RECIBÍ DE:                                            │
│  Nombre: Juan Pérez García                              │
│  RUC: 1234567890                                        │
│                                                         │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  CONCEPTO: Pago parcial de deuda                       │
│                                                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │  Saldo anterior:          $  500.00             │   │
│  │  Monto pagado:            $  150.00             │   │
│  │  ─────────────────────────────────────          │   │
│  │  SALDO NUEVO:             $  350.00             │   │
│  └─────────────────────────────────────────────────┘   │
│                                                         │
│  Forma de pago: [X] Efectivo  [ ] Transferencia        │
│  Referencia: N/A                                        │
│                                                         │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  _____________________    _____________________         │
│  Firma del Trabajador     Firma del Responsable        │
│                                                         │
│  Fecha de emisión: 22/08/2026 14:35:22                 │
└─────────────────────────────────────────────────────────┘
```

---

## 6. API

### Registrar Pago (Modificado)
```
POST /api/pago-deuda
Body: {
  trabajadorId: UUID,
  monto: 150.00,
  formaPago: "EFECTIVO",
  referenciaBancaria: null,
  concepto: "Pago parcial de deuda"
}
Response: {
  id: UUID,
  numeroRecibo: "REC-2026-00001",
  saldoAnterior: 500.00,
  saldoNuevo: 350.00,
  ...
}
```

### Descargar Comprobante
```
GET /api/pago-deuda/{id}/comprobante
Response: application/pdf (descarga)
Headers:
  Content-Disposition: attachment; filename="Recibo_REC-2026-00001.pdf"
```

### Historial de Pagos por Trabajador
```
GET /api/pago-deuda/trabajador/{trabajadorId}
Response: {
  trabajador: { id, nombre, ruc },
  saldoActual: 350.00,
  pagos: [
    { id, numeroRecibo, monto, fecha, formaPago, saldoNuevo },
    ...
  ]
}
```

---

## 7. Servicio de Generación PDF

### ReciboPdfService

```java
@Service
public class ReciboPdfService {

    public byte[] generarComprobante(PagoDeudaDto pago, TrabajadorDto trabajador, FincaDto finca) {
        // Usar PDFBox (como FacturaPdfService existente)
        // 1. Crear documento tamaño media carta
        // 2. Dibujar header con datos de finca
        // 3. Dibujar datos del trabajador
        // 4. Dibujar tabla de montos
        // 5. Dibujar sección de firmas
        // 6. Retornar bytes
    }
}
```

---

## 8. Componentes Vue

### Modificar DeudaTrabajadorList.vue
- Agregar columna "Último pago"
- Botón "Ver historial" que abre modal

### Nuevo: HistorialPagos.vue (Modal)
```vue
<template>
  <div class="historial-pagos">
    <div class="header">
      <h3>Historial de Pagos</h3>
      <div class="saldo-actual">
        Saldo actual: <strong>${{ saldoActual }}</strong>
      </div>
    </div>

    <table>
      <thead>
        <tr>
          <th>No. Recibo</th>
          <th>Fecha</th>
          <th>Monto</th>
          <th>Forma</th>
          <th>Saldo</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="pago in pagos">
          <td>{{ pago.numeroRecibo }}</td>
          <td>{{ formatDate(pago.fecha) }}</td>
          <td>${{ pago.monto }}</td>
          <td>{{ pago.formaPago }}</td>
          <td>${{ pago.saldoNuevo }}</td>
          <td>
            <button @click="descargarRecibo(pago.id)">
              📄 PDF
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
```

---

## 9. Flujo de Registro de Pago

```
1. Usuario abre modal "Registrar Pago" en trabajador X
2. Sistema muestra saldo actual: $500.00
3. Usuario ingresa:
   - Monto: $150.00
   - Forma: Efectivo
   - Concepto: "Pago parcial"
4. Usuario confirma
5. Backend:
   a. Obtener deuda actual del trabajador
   b. Calcular saldo anterior = deuda.importe
   c. Generar número de recibo
   d. Calcular saldo nuevo = saldoAnterior - monto
   e. Crear PagoDeuda con todos los datos
   f. Actualizar deuda.importe = saldoNuevo
   g. Retornar pago creado
6. Frontend muestra confirmación con opción "Descargar recibo"
7. Usuario descarga PDF
```

---

## 10. Migración de Datos

```sql
-- Agregar columnas a pago_deuda
ALTER TABLE pago_deuda
ADD COLUMN numero_recibo VARCHAR(20),
ADD COLUMN saldo_anterior DOUBLE PRECISION,
ADD COLUMN saldo_nuevo DOUBLE PRECISION,
ADD COLUMN concepto VARCHAR(255);

-- Generar números para pagos existentes
WITH numerados AS (
  SELECT id,
         ROW_NUMBER() OVER (ORDER BY fecha) as num,
         EXTRACT(YEAR FROM fecha) as anio
  FROM pago_deuda
  WHERE numero_recibo IS NULL
)
UPDATE pago_deuda p
SET numero_recibo = 'REC-' || n.anio || '-' || LPAD(n.num::text, 5, '0')
FROM numerados n
WHERE p.id = n.id;
```

---

## 11. Criterios de Aceptación

- [ ] Al registrar pago se genera número de recibo automático
- [ ] El PDF se descarga correctamente con todos los datos
- [ ] El saldo se actualiza correctamente (anterior - pago = nuevo)
- [ ] El historial muestra todos los pagos ordenados por fecha
- [ ] Cada pago tiene botón funcional de descarga PDF
- [ ] El PDF incluye espacios para firma
- [ ] Los pagos existentes tienen números asignados

---

## 12. Dependencias

- SPEC-001 (reutilizar patrón de numeración)
- PDFBox (ya existe en el proyecto)
- Modificar: `PagoDeudaService`, `DeudaTrabajadorService`

---

## 13. Riesgos

| Riesgo | Mitigación |
|--------|------------|
| Pago mayor que deuda | Validar monto <= saldoActual |
| Deuda en cero | Marcar como "Pagada" automáticamente |
| Regenerar PDF alterado | Almacenar hash del PDF original |
