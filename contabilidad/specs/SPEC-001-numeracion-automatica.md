# SPEC-001: Numeración Automática de Vales y Facturas

## Metadata
- **ID**: SPEC-001
- **Módulo**: Facturación
- **Prioridad**: ALTA
- **Estado**: Propuesto
- **Fecha**: 2026-08-22

---

## 1. Problema

Actualmente el sistema no genera números automáticos para vales y facturas:
- El campo `numero` en `Salida` es manual o vacío
- No hay validación de secuencia (pueden existir huecos)
- No hay separación por año fiscal
- No hay prefijos distintivos por tipo (VALE vs FACTURA)

### Impacto
- Imposible auditar secuencia de documentos
- Riesgo de duplicados
- No cumple requisitos contables básicos

---

## 2. Requisitos Funcionales

### RF-001: Configuración de Numeración
- El sistema DEBE mantener una tabla de configuración de numeración
- DEBE soportar configuración por: tipo de documento, finca, año
- DEBE permitir definir prefijo personalizado
- DEBE almacenar el último número usado

### RF-002: Generación Automática
- Al crear una Salida, el sistema DEBE generar el número automáticamente
- El formato DEBE ser: `{PREFIJO}-{AÑO}-{SECUENCIA:5 dígitos}`
- Ejemplos: `VALE-2026-00001`, `FAC-2026-00001`
- La secuencia DEBE reiniciarse cada año

### RF-003: Unicidad
- El número generado DEBE ser único por finca + tipo + año
- El sistema DEBE prevenir condiciones de carrera (concurrencia)
- NO DEBE permitir edición manual del número una vez generado

### RF-004: Validación de Secuencia
- El sistema DEBE detectar huecos en la secuencia
- DEBE proveer endpoint para verificar integridad de numeración
- DEBE registrar cualquier anomalía detectada

---

## 3. Requisitos No Funcionales

### RNF-001: Rendimiento
- La generación de número NO DEBE agregar más de 50ms de latencia

### RNF-002: Concurrencia
- DEBE soportar múltiples usuarios creando salidas simultáneamente
- DEBE usar bloqueo optimista o pesimista para evitar duplicados

### RNF-003: Recuperabilidad
- Si falla la creación de Salida, el número NO DEBE quedar "consumido"

---

## 4. Modelo de Datos

### Nueva Entidad: ConfiguracionNumeracion

```java
@Entity
@Table(name = "configuracion_numeracion")
public class ConfiguracionNumeracion {
    @Id
    private UUID id;

    @Column(name = "finca_id", nullable = false)
    private UUID fincaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSalida tipo;  // VALE, FACTURA

    @Column(nullable = false, length = 10)
    private String prefijo;  // "VALE", "FAC"

    @Column(nullable = false)
    private Integer anio;

    @Column(name = "ultimo_numero", nullable = false)
    private Integer ultimoNumero;

    @Version
    private Long version;  // Para bloqueo optimista
}
```

### Índice Único
```sql
CREATE UNIQUE INDEX idx_numeracion_unica
ON configuracion_numeracion(finca_id, tipo, anio);
```

---

## 5. API

### Endpoint Interno (usado por SalidaService)
```
POST /api/internal/numeracion/siguiente
Body: { fincaId, tipo }
Response: { numero: "VALE-2026-00001" }
```

### Endpoint de Verificación
```
GET /api/numeracion/verificar-secuencia?fincaId={}&tipo={}&anio={}
Response: {
  completa: true/false,
  huecos: [3, 7, 15],  // números faltantes
  ultimoNumero: 20
}
```

---

## 6. Flujo de Creación de Salida (Modificado)

```
1. Usuario envía POST /api/salida (sin número)
2. SalidaService.crear():
   a. Obtener/crear ConfiguracionNumeracion para finca+tipo+año
   b. Incrementar ultimoNumero con bloqueo optimista
   c. Generar número formateado
   d. Asignar a Salida.numero
   e. Persistir Salida
   f. Si falla → rollback (número no se pierde por @Transactional)
3. Retornar Salida con número asignado
```

---

## 7. Migración de Datos Existentes

```sql
-- Asignar números a salidas existentes (ordenadas por fecha)
WITH numeradas AS (
  SELECT id, tipo, finca_id,
         ROW_NUMBER() OVER (
           PARTITION BY finca_id, tipo, EXTRACT(YEAR FROM fecha)
           ORDER BY fecha
         ) as num
  FROM salida
  WHERE numero IS NULL OR numero = ''
)
UPDATE salida s
SET numero = CASE
  WHEN n.tipo = 'VALE' THEN 'VALE-' || EXTRACT(YEAR FROM s.fecha) || '-' || LPAD(n.num::text, 5, '0')
  ELSE 'FAC-' || EXTRACT(YEAR FROM s.fecha) || '-' || LPAD(n.num::text, 5, '0')
END
FROM numeradas n
WHERE s.id = n.id;
```

---

## 8. Criterios de Aceptación

- [ ] Al crear una Salida tipo VALE, se genera número `VALE-2026-XXXXX`
- [ ] Al crear una Salida tipo FACTURA, se genera número `FAC-2026-XXXXX`
- [ ] Dos usuarios creando salidas simultáneamente obtienen números diferentes
- [ ] El número NO se puede editar después de creado
- [ ] Al iniciar nuevo año, la secuencia reinicia en 00001
- [ ] Endpoint de verificación detecta huecos correctamente
- [ ] Salidas existentes migradas con números válidos

---

## 9. Dependencias

- Ninguna externa
- Requiere modificar: `SalidaService`, `CreateSalidaCommandHandler`

---

## 10. Riesgos

| Riesgo | Mitigación |
|--------|------------|
| Condición de carrera | Usar @Version + retry |
| Pérdida de números | Transacción atómica |
| Año fiscal diferente | Configurar mes de corte (futuro) |
