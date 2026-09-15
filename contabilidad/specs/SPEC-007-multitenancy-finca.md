# SPEC-007: Multi-Tenancy por Finca

## Resumen

Implementar filtrado automático por Finca (tenant) en todo el sistema. Cada usuario ve y opera solo datos de su finca, excepto ADMIN que tiene visibilidad global.

## Cadena de Relación

```
Usuario → Trabajador → Finca (tenant)
```

## Reglas de Acceso por Rol

| Rol | Lectura | Escritura |
|-----|---------|-----------|
| ADMIN | Todas las fincas (con selector) | Cualquier finca (elige cuál) |
| RESPONSABLE | Solo su finca | Solo su finca (implícita) |
| USER | Solo su finca | Solo su finca (implícita) |

## Clasificación de Entidades

### Entidades Globales (sin filtro)
- `Producto` — catálogo general
- `Cargo` — catálogo de cargos
- `Grupo` — catálogo de grupos
- `Finca` — lista de tenants

### Entidades Filtradas por Finca

| Entidad | Campo de Filtro | Tipo |
|---------|-----------------|------|
| Trabajador | `fincaId` | Directo |
| FincaProducto | `fincaId` | Directo |
| ProduccionTerminada | `fincaId` | Directo |
| Reporte | `fincaId` | Directo |
| Usuario | `trabajador.fincaId` | Vía relación |
| Salida | `fincaProducto.fincaId` | Vía relación |
| DeudaTrabajador | `trabajador.fincaId` | Vía relación |
| Auditoria | — | Por usuario logueado |
| MovimientoStock | `fincaProducto.fincaId` | Vía relación |

## Implementación Backend

### 1. Modificar JWT Claims

**Archivo:** `JwtService.java`

Agregar claims al token:
```java
Map<String, Object> claims = new HashMap<>();
claims.put("fincaId", usuario.getTrabajador().getFincaId().toString());
claims.put("rol", usuario.getRol().name());
claims.put("trabajadorId", usuario.getTrabajadorId().toString());
```

### 2. Crear TenantContext

**Archivo:** `TenantContext.java`

```java
public class TenantContext {
    private static final ThreadLocal<TenantInfo> context = new ThreadLocal<>();

    public static void set(TenantInfo info) { context.set(info); }
    public static TenantInfo get() { return context.get(); }
    public static void clear() { context.remove(); }

    public static UUID getFincaId() {
        return get() != null ? get().getFincaId() : null;
    }
    public static boolean isAdmin() {
        return get() != null && get().getRol() == Rol.ADMIN;
    }
}

@Data @Builder
public class TenantInfo {
    private UUID usuarioId;
    private UUID fincaId;
    private UUID trabajadorId;
    private Rol rol;
    private UUID fincaSeleccionada; // Para ADMIN que filtra por finca
}
```

### 3. Modificar JwtAuthenticationFilter

**Archivo:** `JwtAuthenticationFilter.java`

Después de validar el token, extraer claims y setear TenantContext:
```java
String fincaId = jwtService.extractClaim(token, c -> c.get("fincaId", String.class));
String rol = jwtService.extractClaim(token, c -> c.get("rol", String.class));

TenantInfo info = TenantInfo.builder()
    .usuarioId(usuario.getId())
    .fincaId(UUID.fromString(fincaId))
    .rol(Rol.valueOf(rol))
    .build();
TenantContext.set(info);
```

Limpiar al finalizar request (en finally o con filtro de cleanup).

### 4. Crear TenantSpecification Helper

**Archivo:** `TenantSpecification.java`

```java
public class TenantSpecification {

    public static <T> Specification<T> forFincaDirecta() {
        return (root, query, cb) -> {
            if (TenantContext.isAdmin() && TenantContext.getFincaSeleccionada() == null) {
                return cb.conjunction(); // Sin filtro
            }
            UUID fincaId = TenantContext.getFincaSeleccionada() != null
                ? TenantContext.getFincaSeleccionada()
                : TenantContext.getFincaId();
            return cb.equal(root.get("fincaId"), fincaId);
        };
    }

    public static <T> Specification<T> forFincaViaTrabajador() {
        return (root, query, cb) -> {
            if (TenantContext.isAdmin() && TenantContext.getFincaSeleccionada() == null) {
                return cb.conjunction();
            }
            UUID fincaId = TenantContext.getFincaSeleccionada() != null
                ? TenantContext.getFincaSeleccionada()
                : TenantContext.getFincaId();
            return cb.equal(root.get("trabajador").get("fincaId"), fincaId);
        };
    }

    public static <T> Specification<T> forFincaViaFincaProducto() {
        return (root, query, cb) -> {
            if (TenantContext.isAdmin() && TenantContext.getFincaSeleccionada() == null) {
                return cb.conjunction();
            }
            UUID fincaId = TenantContext.getFincaSeleccionada() != null
                ? TenantContext.getFincaSeleccionada()
                : TenantContext.getFincaId();
            return cb.equal(root.get("fincaProducto").get("fincaId"), fincaId);
        };
    }
}
```

### 5. Modificar Servicios

Agregar `.and(TenantSpecification.forFincaXXX())` en cada método search():

```java
// Ejemplo: TrabajadorServiceImpl.search()
Specification<Trabajador> spec = Specification
    .where(specifications)
    .and(activoSpec)
    .and(TenantSpecification.forFincaDirecta()); // NUEVO

Page<Trabajador> data = repositoryQuery.findAll(spec, pageable);
```

### 6. Validación en Escritura

En operaciones create/update, validar que el usuario puede escribir en esa finca:

```java
public class TenantValidator {
    public static void validateWrite(UUID targetFincaId) {
        if (TenantContext.isAdmin()) return; // ADMIN puede todo

        if (!TenantContext.getFincaId().equals(targetFincaId)) {
            throw new AccessDeniedException("No tiene permiso para operar en esta finca");
        }
    }

    public static UUID getWriteFincaId() {
        // Para USER/RESPONSABLE, siempre es su finca
        if (!TenantContext.isAdmin()) {
            return TenantContext.getFincaId();
        }
        // Para ADMIN, debe especificar o usar la seleccionada
        return TenantContext.getFincaSeleccionada();
    }
}
```

### 7. Endpoint para Selección de Finca (ADMIN)

**Archivo:** `FincaController.java`

```java
@PostMapping("/seleccionar/{fincaId}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> seleccionarFinca(@PathVariable UUID fincaId) {
    // Valida que existe
    fincaService.findById(fincaId);
    // Retorna confirmación (el frontend guarda en estado local)
    return ResponseEntity.ok(Map.of("fincaId", fincaId));
}
```

## Implementación Frontend

### 1. Almacenar Finca en AuthService

```typescript
// AuthService.ts
interface UserInfo {
  // ... existing fields
  fincaId: string;
  fincaName: string;
  fincaSeleccionada?: string; // Para ADMIN
}

setFincaSeleccionada(fincaId: string) {
  const user = this.getUser();
  if (user) {
    user.fincaSeleccionada = fincaId;
    localStorage.setItem('user', JSON.stringify(user));
  }
}
```

### 2. Header con Selector de Finca (ADMIN)

```vue
<!-- App.vue - Solo visible para ADMIN -->
<div v-if="isAdmin" class="finca-selector">
  <select v-model="fincaSeleccionada" @change="cambiarFinca">
    <option value="">Todas las fincas</option>
    <option v-for="f in fincas" :key="f.id" :value="f.id">
      {{ f.name }}
    </option>
  </select>
</div>
```

### 3. Enviar Finca Seleccionada en Requests

```typescript
// En axios interceptor
axios.interceptors.request.use(config => {
  const user = AuthService.getUser();
  if (user?.fincaSeleccionada) {
    config.headers['X-Finca-Id'] = user.fincaSeleccionada;
  }
  return config;
});
```

### 4. Backend Lee Header X-Finca-Id

```java
// JwtAuthenticationFilter
String fincaSeleccionada = request.getHeader("X-Finca-Id");
if (fincaSeleccionada != null && TenantContext.isAdmin()) {
    TenantContext.get().setFincaSeleccionada(UUID.fromString(fincaSeleccionada));
}
```

## Lista de Archivos a Modificar

### Backend - Nuevos
- `domain/dto/TenantInfo.java`
- `infrastructure/security/TenantContext.java`
- `infrastructure/security/TenantSpecification.java`
- `infrastructure/security/TenantValidator.java`
- `infrastructure/security/TenantCleanupFilter.java`

### Backend - Modificar
- `infrastructure/services/JwtService.java` — agregar claims
- `infrastructure/security/JwtAuthenticationFilter.java` — setear TenantContext
- `controller/AuthController.java` — pasar Usuario completo a generateToken
- `infrastructure/services/TrabajadorServiceImpl.java` — agregar tenant spec
- `infrastructure/services/FincaProductoServiceImpl.java` — agregar tenant spec
- `infrastructure/services/SalidaServiceImpl.java` — agregar tenant spec
- `infrastructure/services/ProduccionTerminadaServiceImpl.java` — agregar tenant spec
- `infrastructure/services/DeudaTrabajadorServiceImpl.java` — agregar tenant spec
- `infrastructure/services/ReporteServiceImpl.java` — agregar tenant spec
- `infrastructure/services/AuditoriaServiceImpl.java` — agregar tenant spec
- `infrastructure/services/MovimientoStockServiceImpl.java` — agregar tenant spec
- `infrastructure/services/UsuarioServiceImpl.java` — agregar tenant spec

### Frontend - Modificar
- `services/AuthService.ts` — manejar fincaSeleccionada
- `App.vue` — agregar selector de finca para ADMIN
- `services/*.ts` — (opcional) pasar header X-Finca-Id via interceptor

## Orden de Implementación

1. **Fase 1 - Core:** TenantContext, TenantInfo, TenantSpecification
2. **Fase 2 - JWT:** Modificar JwtService y JwtAuthenticationFilter
3. **Fase 3 - Servicios:** Agregar tenant filter a cada servicio
4. **Fase 4 - Validación:** TenantValidator para escrituras
5. **Fase 5 - Frontend:** Selector de finca y header X-Finca-Id

## Testing

- Usuario USER solo ve datos de su finca
- Usuario RESPONSABLE solo ve datos de su finca
- Usuario ADMIN sin filtro ve todo
- Usuario ADMIN con X-Finca-Id ve solo esa finca
- Escritura fuera de finca rechazada para USER/RESPONSABLE
- ADMIN puede escribir en cualquier finca
