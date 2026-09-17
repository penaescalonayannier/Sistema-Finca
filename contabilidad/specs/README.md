# Especificaciones del Sistema Finca

## Resumen

Este directorio contiene las especificaciones técnicas para mejoras del sistema.

| ID | Nombre | Módulo | Prioridad | Estado |
|----|--------|--------|-----------|--------|
| [SPEC-001](./SPEC-001-numeracion-automatica.md) | Numeración Automática de Vales/Facturas | Facturación | ALTA | Propuesto |
| [SPEC-002](./SPEC-002-comprobante-pago.md) | Comprobante de Pago de Deuda (PDF) | Recibos | ALTA | Propuesto |
| [SPEC-003](./SPEC-003-alertas-stock.md) | Alertas de Stock Mínimo | Inventario | MEDIA | Propuesto |
| [SPEC-004](./SPEC-004-reportes-consolidados.md) | Reportes Consolidados | Reportes | MEDIA | Propuesto |
| [Registro de formas numeradas](./REGISTRO_FORMAS_NUMERADAS_RECOMENDACION.md) | Control documental por tipo y serie | Contabilidad / Caja / Almacén | ALTA | Implementación inicial |
| [Mapa de formas Caja/Banco](./MAPA_FORMAS_NUMERADAS_CAJA_BANCO.md) | Códigos y alcance de formas de caja y banco | Caja / Banco | ALTA | En progreso |

---

## Diagrama de Dependencias

```
SPEC-001 (Numeración)
    │
    └──► SPEC-002 (Comprobantes) [reutiliza patrón]

SPEC-003 (Alertas Stock) ──► independiente

SPEC-004 (Reportes) ──► independiente
```

---

## Plan de Implementación Sugerido

### Fase 1: Fundamentos (Semana 1-2)
1. **SPEC-001**: Numeración automática
   - Crear entidad ConfiguracionNumeracion
   - Modificar SalidaService
   - Migrar datos existentes

### Fase 2: Comprobantes (Semana 2-3)
2. **SPEC-002**: Comprobantes de pago
   - Crear ReciboPdfService
   - Modificar PagoDeudaService
   - Crear UI de historial

### Fase 3: Inventario (Semana 3-4)
3. **SPEC-003**: Alertas de stock
   - Agregar campos stockMinimo
   - Crear endpoint de alertas
   - Dashboard de alertas

### Fase 4: Reportes (Semana 4-5)
4. **SPEC-004**: Reportes consolidados
   - Reporte de deudas pendientes
   - Reporte de facturación
   - Kardex consolidado
   - Gráficos de movimientos

---

## Estimación de Esfuerzo

| SPEC | Backend | Frontend | Testing | Total |
|------|---------|----------|---------|-------|
| 001 | 8h | 2h | 4h | 14h |
| 002 | 12h | 8h | 4h | 24h |
| 003 | 8h | 12h | 4h | 24h |
| 004 | 16h | 20h | 8h | 44h |
| **Total** | **44h** | **42h** | **20h** | **106h** |

---

## Estados

- **Propuesto**: Especificación inicial creada
- **Aprobado**: Revisado y listo para implementar
- **En Progreso**: Implementación activa
- **Completado**: Implementado y probado
- **Cancelado**: Descartado

---

## Cómo Usar

1. Leer la especificación completa antes de implementar
2. Validar criterios de aceptación al finalizar
3. Actualizar estado en este README
4. Documentar cualquier desviación de la spec

---

## Changelog

| Fecha | Cambio |
|-------|--------|
| 2026-08-22 | Creación inicial de specs 001-004 |
