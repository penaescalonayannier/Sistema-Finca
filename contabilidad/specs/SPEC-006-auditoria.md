# SPEC-006: Sistema de Auditoría

## Metadata
- **ID**: SPEC-006
- **Módulo**: Seguridad/Auditoría
- **Prioridad**: ALTA
- **Estado**: Propuesto
- **Fecha**: 2026-08-23

---

## 1. Problema

El sistema no registra quién realizó cada operación:
- No hay trazabilidad de cambios en los datos
- No se puede identificar quién creó, modificó o eliminó registros
- Ante un problema, no hay forma de saber qué pasó
- No existe historial de acciones para análisis

### Impacto
- Falta de accountability (responsabilidad)
- Dificultad para detectar errores o fraudes
- No cumple requisitos básicos de auditoría empresarial
- Imposible rastrear problemas o revertir cambios incorrectos

---

## 2. Requisitos Funcionales

### RF-001: Registro de Acciones
- DEBE registrar automáticamente cada operación CRUD en entidades principales
- DEBE capturar: usuario, acción, entidad, ID afectado, timestamp
- DEBE capturar valores antes/después para modificaciones (JSON)

### RF-002: Entidades a Auditar
- Trabajador (crear, modificar, eliminar, reactivar, transferir)
- Finca (crear, modificar, eliminar, asignar responsable)
- Producto (crear, modificar, eliminar)
- FincaProducto (asignar, ajuste stock, entradas, salidas)
- Usuario (crear, modificar, cambiar password, activar/desactivar)
- Salida (crear, anular)
- ProduccionTerminada (crear, modificar, eliminar)
- DeudaTrabajador (crear, pago)

### RF-003: Tipos de Acción
- CREATE: Creación de registro
- UPDATE: Modificación de registro
- DELETE: Eliminación (soft delete)
- LOGIN: Inicio de sesión
- LOGOUT: Cierre de sesión
- EXPORT: Exportación de datos
- STOCK_ADJUSTMENT: Ajuste de inventario
- PAYMENT: Registro de pago

### RF-004: Consulta de Auditoría
- Solo ADMIN puede ver registros de auditoría
- Filtrar por: usuario, entidad, acción, rango de fechas
- Búsqueda por texto en valores
- Paginación obligatoria (evitar cargas masivas)

### RF-005: Vista de Auditoría
- Tabla con historial de acciones
- Filtros por usuario, tipo de acción, entidad, fechas
- Expandir fila para ver detalles (valores antes/después)
- Exportar a Excel

---

## 3. Requisitos No Funcionales

### RNF-001: Rendimiento
- Registro de auditoría NO DEBE impactar > 10ms por operación
- Registro DEBE ser asíncrono si es posible
- Consulta DEBE responder < 500ms con paginación

### RNF-002: Almacenamiento
- Valores antes/después como JSONB (compacto)
- Índices en: usuario_id, entidad, created_at
- Considerar particionamiento por fecha si crece mucho

### RNF-003: Integridad
- Registros de auditoría NO DEBEN poder eliminarse
- NO DEBEN poder modificarse una vez creados
- Debe persistir incluso si la transacción principal falla

---

## 4. Modelo de Datos

### Nueva Entidad: Auditoria

```java
@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    private UUID id;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "username", length = 50)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "accion", nullable = false)
    private TipoAccion accion;

    @Column(name = "entidad", nullable = false, length = 50)
    private String entidad;

    @Column(name = "entidad_id")
    private UUID entidadId;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior; // JSON

    @Column(name = "valor_nuevo", columnDefinition = "TEXT")
    private String valorNuevo; // JSON

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
```

### Enum TipoAccion

```java
public enum TipoAccion {
    CREATE,
    UPDATE,
    DELETE,
    LOGIN,
    LOGOUT,
    EXPORT,
    STOCK_ADJUSTMENT,
    PAYMENT,
    REACTIVATE,
    TRANSFER
}
```

---

## 5. Implementación

### 5.1 Backend

#### AuditoriaService
```java
@Service
public class AuditoriaServiceImpl implements IAuditoriaService {

    void registrar(TipoAccion accion, String entidad, UUID entidadId,
                   String descripcion, Object valorAnterior, Object valorNuevo);

    PaginatedResponse buscar(Pageable pageable, List<FilterCriteria> filtros);
}
```

#### Integración con SecurityContext
- Obtener usuario actual de `SecurityContextHolder`
- Obtener IP de `HttpServletRequest`

#### Interceptor/AOP
- Usar `@Around` para interceptar métodos de servicios
- O llamar manualmente desde cada handler

### 5.2 Frontend

#### Vista Auditoría (`/auditoria`)
- Solo visible para ADMIN
- Tabla con columnas: Fecha, Usuario, Acción, Entidad, ID, Descripción
- Filtros: Usuario (select), Acción (select), Entidad (select), Fechas (rango)
- Click en fila expande para mostrar valores antes/después en JSON formateado
- Botón exportar a Excel

---

## 6. Endpoints API

### GET /api/auditoria/search
- Body: `{ filter: [], query: "", pageSize: 20, page: 0, sortBy: "createdAt", sortType: "DESC" }`
- Response: `PaginatedResponse<AuditoriaResponse>`
- Requiere: ROLE_ADMIN

### GET /api/auditoria/{id}
- Response: `AuditoriaResponse` con valores completos
- Requiere: ROLE_ADMIN

### GET /api/auditoria/export
- Query params: filtros
- Response: Excel file
- Requiere: ROLE_ADMIN

---

## 7. Migración de Datos

```sql
CREATE TABLE auditoria (
    id UUID PRIMARY KEY,
    usuario_id UUID,
    username VARCHAR(50),
    accion VARCHAR(30) NOT NULL,
    entidad VARCHAR(50) NOT NULL,
    entidad_id UUID,
    descripcion VARCHAR(500),
    valor_anterior TEXT,
    valor_nuevo TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_auditoria_usuario ON auditoria(usuario_id);
CREATE INDEX idx_auditoria_entidad ON auditoria(entidad);
CREATE INDEX idx_auditoria_created_at ON auditoria(created_at);
CREATE INDEX idx_auditoria_accion ON auditoria(accion);
```

---

## 8. Checklist de Implementación

- [ ] Crear enum TipoAccion
- [ ] Crear entidad Auditoria
- [ ] Crear AuditoriaDto
- [ ] Crear repositorios (read/write)
- [ ] Crear IAuditoriaService y AuditoriaServiceImpl
- [ ] Crear AuditoriaController
- [ ] Integrar registro en servicios existentes (usuarios, trabajadores, fincas, etc.)
- [ ] Crear AuditoriaService.ts en frontend
- [ ] Crear AuditoriaList.vue
- [ ] Agregar ruta y navegación
- [ ] Probar flujo completo
