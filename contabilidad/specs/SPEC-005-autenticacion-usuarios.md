# SPEC-005: Autenticación de Usuarios

## Metadata
- **ID**: SPEC-005
- **Módulo**: Seguridad/Autenticación
- **Prioridad**: ALTA
- **Estado**: Propuesto
- **Fecha**: 2026-08-23

---

## 1. Problema

El sistema actualmente no tiene control de acceso:
- Cualquier persona puede acceder a todas las funcionalidades
- No hay registro de quién realiza cada operación
- No existe forma de restringir acceso por rol o finca
- Los datos sensibles están expuestos sin autenticación

### Impacto
- Riesgo de manipulación de datos no autorizada
- Imposibilidad de auditar acciones
- Violación de principios básicos de seguridad
- No se puede personalizar la experiencia por usuario

---

## 2. Requisitos Funcionales

### RF-001: Entidad Usuario
- Un Usuario DEBE estar asociado a un Trabajador existente
- Un Trabajador PUEDE tener máximo un Usuario
- Usuario DEBE tener: username, password, rol
- Password DEBE almacenarse encriptado (BCrypt)

### RF-002: Roles de Usuario
- **ADMIN**: Acceso total al sistema
- **RESPONSABLE**: Acceso a su finca y operaciones de gestión
- **USER**: Acceso básico de consulta

### RF-003: Registro de Usuario
- Solo ADMIN puede crear usuarios
- DEBE seleccionar un Trabajador existente sin usuario asignado
- DEBE asignar username único y contraseña
- DEBE asignar rol inicial

### RF-004: Login
- Pantalla de login al cargar la aplicación
- Autenticación con username y password
- Generar token JWT válido por 24 horas
- Registrar fecha/hora de último login

### RF-005: Protección de Rutas
- Todas las rutas (excepto login) requieren autenticación
- Backend valida token JWT en cada petición
- Frontend redirige a login si no hay token válido

### RF-006: Logout
- Eliminar token del cliente
- Redirigir a pantalla de login

### RF-007: Gestión de Usuarios (Admin)
- Listar usuarios activos
- Crear nuevo usuario
- Editar rol de usuario
- Desactivar usuario (soft delete)
- Cambiar contraseña

---

## 3. Requisitos No Funcionales

### RNF-001: Seguridad
- Passwords DEBEN usar BCrypt con strength >= 10
- Tokens JWT DEBEN tener firma HMAC-SHA256
- Secret key DEBE ser configurable vía properties

### RNF-002: Rendimiento
- Login DEBE responder en menos de 500ms
- Validación de token DEBE ser < 50ms

### RNF-003: Experiencia de Usuario
- Mostrar mensaje claro en credenciales inválidas
- Mostrar nombre del usuario logueado en header
- Recordar sesión mientras el token sea válido

---

## 4. Modelo de Datos

### Nueva Entidad: Usuario

```java
@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;  // BCrypt encoded

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "trabajador_id", unique = true, nullable = false)
    private UUID trabajadorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trabajador_id", insertable = false, updatable = false)
    private Trabajador trabajador;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}
```

### Enum: Rol

```java
public enum Rol {
    ADMIN,       // Acceso total
    RESPONSABLE, // Gestión de finca
    USER         // Solo consultas
}
```

### Diagrama de Relaciones

```
┌─────────────┐     1:1     ┌─────────────┐
│   Usuario   │────────────▶│  Trabajador │
└─────────────┘             └─────────────┘
       │                           │
       │                           │
       ▼                           ▼
   [tiene]                    [pertenece a]
       │                           │
       ▼                           ▼
┌─────────────┐             ┌─────────────┐
│     Rol     │             │    Finca    │
└─────────────┘             └─────────────┘
```

---

## 5. DTOs

### UsuarioDto

```java
public class UsuarioDto {
    private UUID id;
    private String username;
    private String password;     // Solo para create/update
    private Rol rol;
    private Boolean activo;
    private UUID trabajadorId;
    private String trabajadorNombre;
    private String trabajadorRuc;
    private UUID fincaId;
    private String fincaName;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
```

### LoginRequest

```java
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
```

### LoginResponse

```java
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private UUID usuarioId;
    private String username;
    private Rol rol;
    private UUID trabajadorId;
    private String trabajadorNombre;
    private UUID fincaId;
    private String fincaName;
    private long expiresIn;  // milliseconds
}
```

### RegisterRequest

```java
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(min = 4)
    private String password;

    @NotNull
    private UUID trabajadorId;

    private Rol rol = Rol.USER;
}
```

---

## 6. API

### Autenticación (Públicos)

#### Login
```
POST /api/auth/login
Body: {
    "username": "jperez",
    "password": "miPassword123"
}
Response: {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "type": "Bearer",
    "usuarioId": "uuid",
    "username": "jperez",
    "rol": "USER",
    "trabajadorId": "uuid",
    "trabajadorNombre": "Juan Pérez",
    "fincaId": "uuid",
    "fincaName": "Finca Norte",
    "expiresIn": 86400000
}
```

#### Validar Token
```
GET /api/auth/validate
Headers: Authorization: Bearer <token>
Response: {
    "valid": true,
    "usuario": { ... }
}
```

### Gestión de Usuarios (Solo ADMIN)

#### Listar Usuarios
```
GET /api/usuarios
Response: [UsuarioDto, ...]
```

#### Obtener Usuario
```
GET /api/usuarios/{id}
Response: UsuarioDto
```

#### Crear Usuario
```
POST /api/usuarios
Body: RegisterRequest
Response: UsuarioDto
```

#### Actualizar Usuario
```
PUT /api/usuarios/{id}
Body: {
    "username": "nuevo_username",
    "rol": "RESPONSABLE"
}
Response: UsuarioDto
```

#### Cambiar Contraseña
```
PATCH /api/usuarios/{id}/password
Body: {
    "newPassword": "nuevaContraseña"
}
Response: { "message": "Password updated" }
```

#### Desactivar Usuario
```
DELETE /api/usuarios/{id}
Response: { "message": "Usuario desactivado" }
```

#### Trabajadores Disponibles
```
GET /api/usuarios/trabajadores-disponibles
Response: [TrabajadorDto, ...]  // Solo los que no tienen usuario
```

---

## 7. Configuración Spring Security

### SecurityConfig

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
```

### JwtAuthenticationFilter

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

### JwtService

```java
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")  // 24 hours default
    private long jwtExpiration;

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSignInKey())
            .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
}
```

---

## 8. Componentes Vue

### LoginView.vue

```vue
<template>
  <div class="login-container">
    <div class="login-card">
      <div class="logo">
        <h1>🌱 Sistema Finca</h1>
      </div>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>Usuario</label>
          <input
            v-model="credentials.username"
            type="text"
            placeholder="Ingrese su usuario"
            required
          />
        </div>

        <div class="form-group">
          <label>Contraseña</label>
          <input
            v-model="credentials.password"
            type="password"
            placeholder="Ingrese su contraseña"
            required
          />
        </div>

        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <button type="submit" :disabled="loading">
          {{ loading ? 'Ingresando...' : 'Ingresar' }}
        </button>
      </form>
    </div>
  </div>
</template>
```

### AuthService.ts

```typescript
import axios from 'axios'

interface LoginRequest {
  username: string
  password: string
}

interface LoginResponse {
  token: string
  type: string
  usuarioId: string
  username: string
  rol: string
  trabajadorId: string
  trabajadorNombre: string
  fincaId: string
  fincaName: string
  expiresIn: number
}

class AuthService {
  private readonly TOKEN_KEY = 'auth_token'
  private readonly USER_KEY = 'auth_user'

  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await axios.post<LoginResponse>('/api/auth/login', credentials)
    this.setToken(response.data.token)
    this.setUser(response.data)
    return response.data
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY)
    localStorage.removeItem(this.USER_KEY)
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY)
  }

  setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token)
  }

  getUser(): LoginResponse | null {
    const user = localStorage.getItem(this.USER_KEY)
    return user ? JSON.parse(user) : null
  }

  setUser(user: LoginResponse): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user))
  }

  isAuthenticated(): boolean {
    const token = this.getToken()
    if (!token) return false

    // Verificar si el token no ha expirado
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      return payload.exp * 1000 > Date.now()
    } catch {
      return false
    }
  }

  hasRole(role: string): boolean {
    const user = this.getUser()
    return user?.rol === role
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN')
  }
}

export default new AuthService()
```

### Axios Interceptor

```typescript
// src/plugins/axios.ts
import axios from 'axios'
import AuthService from '@/services/AuthService'
import router from '@/router'

axios.interceptors.request.use(
  (config) => {
    const token = AuthService.getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      AuthService.logout()
      router.push('/login')
    }
    return Promise.reject(error)
  }
)
```

### Router Guard

```typescript
// src/router/index.ts
import AuthService from '@/services/AuthService'

router.beforeEach((to, from, next) => {
  const publicPages = ['/login']
  const authRequired = !publicPages.includes(to.path)

  if (authRequired && !AuthService.isAuthenticated()) {
    return next('/login')
  }

  // Verificar rol para rutas de admin
  if (to.meta.requiresAdmin && !AuthService.isAdmin()) {
    return next('/')
  }

  next()
})
```

### Header con Usuario

```vue
<!-- AppHeader.vue -->
<template>
  <header class="app-header">
    <div class="logo">Sistema Finca</div>

    <div class="user-info" v-if="user">
      <span class="user-name">{{ user.trabajadorNombre }}</span>
      <span class="user-role">{{ user.rol }}</span>
      <span class="user-finca">{{ user.fincaName }}</span>
      <button @click="logout" class="btn-logout">Salir</button>
    </div>
  </header>
</template>
```

---

## 9. Migración SQL

```sql
-- Crear tabla usuario
CREATE TABLE usuario (
    id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMIN', 'RESPONSABLE', 'USER')),
    activo BOOLEAN NOT NULL DEFAULT true,
    trabajador_id UUID NOT NULL UNIQUE REFERENCES trabajador(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Índices
CREATE INDEX idx_usuario_username ON usuario(username);
CREATE INDEX idx_usuario_trabajador ON usuario(trabajador_id);
CREATE INDEX idx_usuario_activo ON usuario(activo);

-- Usuario admin inicial (password: admin123)
-- BCrypt hash de 'admin123' con strength 10
INSERT INTO usuario (id, username, password, rol, activo, trabajador_id, created_at)
SELECT
    gen_random_uuid(),
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGbL8U5CvQdCkKE5.vO6xqHqMbHO',
    'ADMIN',
    true,
    (SELECT id FROM trabajador WHERE activo = true LIMIT 1),
    CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM trabajador WHERE activo = true);
```

---

## 10. Properties

```properties
# JWT Configuration
jwt.secret=c2lzdGVtYS1maW5jYS1zZWNyZXQta2V5LXByb2R1Y3Rpb24tMjAyNi1tdXN0LWJlLWF0LWxlYXN0LTI1Ni1iaXRz
jwt.expiration=86400000

# Security
spring.security.user.name=disabled
spring.security.user.password=disabled
```

---

## 11. Criterios de Aceptación

### Backend
- [ ] Entidad Usuario creada con relación a Trabajador
- [ ] Endpoint /api/auth/login funciona correctamente
- [ ] Token JWT se genera con expiración de 24h
- [ ] Todas las rutas (excepto /api/auth/*) requieren token
- [ ] Solo ADMIN puede acceder a /api/usuarios/*
- [ ] Password se almacena con BCrypt

### Frontend
- [ ] Pantalla de login se muestra al cargar sin token
- [ ] Login exitoso guarda token y redirige al home
- [ ] Login fallido muestra mensaje de error
- [ ] Header muestra nombre del usuario logueado
- [ ] Botón logout elimina token y redirige a login
- [ ] Peticiones sin token redirigen a login
- [ ] Menú de usuarios solo visible para ADMIN

---

## 12. Dependencias Backend

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
```

---

## 13. Archivos a Crear/Modificar

### Backend (Nuevos)
| Archivo | Descripción |
|---------|-------------|
| `domain/dto/Rol.java` | Enum de roles |
| `domain/dto/UsuarioDto.java` | DTO de usuario |
| `infrastructure/entity/Usuario.java` | Entidad JPA |
| `infrastructure/repository/*/UsuarioRepository.java` | Repositorios R/W |
| `domain/services/IUsuarioService.java` | Interface servicio |
| `infrastructure/services/UsuarioServiceImpl.java` | Implementación |
| `infrastructure/services/JwtService.java` | Servicio JWT |
| `infrastructure/config/SecurityConfig.java` | Configuración Spring Security |
| `infrastructure/config/JwtAuthenticationFilter.java` | Filtro JWT |
| `applications/command/auth/*.java` | DTOs de auth |
| `controller/AuthController.java` | Endpoints de auth |
| `controller/UsuarioController.java` | CRUD usuarios |

### Frontend (Nuevos)
| Archivo | Descripción |
|---------|-------------|
| `types/Auth.ts` | Interfaces TypeScript |
| `services/AuthService.ts` | Servicio de autenticación |
| `views/LoginView.vue` | Página de login |
| `components/UsuarioList.vue` | Lista de usuarios (admin) |
| `components/CrearUsuario.vue` | Formulario crear usuario |
| `components/AppHeader.vue` | Header con info usuario |
| `plugins/axios.ts` | Interceptor de Axios |

### Frontend (Modificar)
| Archivo | Cambio |
|---------|--------|
| `router/index.ts` | Agregar guards y ruta login |
| `App.vue` | Condicionar header si logueado |
| `main.ts` | Importar plugin axios |

---

## 14. Fases de Implementación

### Fase 1: Backend Auth (Prioridad 1)
1. Agregar dependencias JWT a pom.xml
2. Crear entidad Usuario
3. Crear repositorios y servicio
4. Implementar JwtService
5. Configurar Spring Security
6. Crear AuthController
7. Ejecutar migración SQL

### Fase 2: Frontend Login (Prioridad 1)
1. Crear AuthService.ts
2. Crear LoginView.vue
3. Configurar interceptor Axios
4. Agregar guards al router
5. Modificar App.vue para condicionar layout

### Fase 3: Gestión Usuarios (Prioridad 2)
1. Crear UsuarioController
2. Crear componentes de gestión
3. Agregar rutas protegidas por rol

---

## 15. Extensiones Futuras

| Feature | Descripción |
|---------|-------------|
| Refresh tokens | Renovar token sin re-login |
| Recuperar contraseña | Email con link de reset |
| Bloqueo por intentos | Bloquear tras N intentos fallidos |
| Auditoría | Log de todas las acciones por usuario |
| 2FA | Autenticación de dos factores |
| Permisos granulares | Permisos específicos por operación |
