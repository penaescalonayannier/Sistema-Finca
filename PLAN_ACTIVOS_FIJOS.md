# Plan de Implementación: Módulo de Activos Fijos Tangibles

## Análisis del Expediente Excel

### Grupos de AFT identificados (según contabilidad cubana):

| Grupo | Nombre | Tasa Depreciación | Campos Específicos |
|-------|--------|-------------------|-------------------|
| 01 | Edificios | 2-3% anual | Estado técnico (%) |
| 02 | Otras Construcciones | 5% anual | Pozos, tanques |
| 04 | Máquinas y Equipos | 10% anual | Tractores, arados, carretas |
| 05 | Aparatos | 20% anual | Computadoras, básculas |
| 07 | Muebles y Objetos | 10% anual | Sillas, mesas, ventiladores |
| 08 | Animales | Variable (4-12 años) | Categoría animal, años vida |
| 12 | Plantaciones Caña | Según cepa | Bloque, campo, área, cepa, variedad |
| 13 | Plantaciones Permanentes | Según cultivo | Plátanos, frutales, área |

### Normativa aplicable:
- **Resolución 1038/2017 MFP**: NCC No. 7 "Activos Fijos Tangibles"
- **Resolución 51/2021 MFP**: Tasas máximas de depreciación
- **Resolución 60/2011 CGR**: Control interno de recursos

---

## Fase 1: Modelo de Datos Base

### 1.1 Entidad: GrupoActivoFijo
```java
@Entity
public class GrupoActivoFijo {
    UUID id;
    String codigo;           // "01", "02", "04", etc.
    String nombre;           // "Edificios", "Otras Construcciones"
    BigDecimal tasaDepreciacion;  // Tasa anual %
    Integer vidaUtilAnios;   // Vida útil por defecto
    Boolean activo;
}
```

### 1.2 Entidad: ActivoFijoTangible
```java
@Entity
public class ActivoFijoTangible {
    UUID id;
    String numeroInventario;      // "01-0008", "04-1503"
    String descripcion;           // "Edificio Comedor"
    GrupoActivoFijo grupo;        // FK al grupo
    Finca finca;                  // FK a qué finca pertenece

    // Valores contables
    BigDecimal valorAdquisicion;
    BigDecimal depreciacionAcumulada;
    BigDecimal valorResidual;     // Calculado: adquisición - depreciación

    // Estado
    Integer estadoTecnicoPorcentaje;  // 0-100%
    BigDecimal valorTasacion;     // Valor de mercado/venta

    // Control
    LocalDate fechaAdquisicion;
    LocalDate fechaBaja;          // Null si activo
    String destino;               // "ECTE", ubicación
    String observaciones;
    Boolean activo;
}
```

### 1.3 Entidad: ActivoAnimal (hereda o extiende AFT)
```java
@Entity
public class ActivoAnimal {
    UUID id;
    ActivoFijoTangible activoBase;  // FK

    // Específico animales
    CategoriaAnimal categoria;    // TERNERO, AÑOJO, TORETE, BUEY, VACA, etc.
    Integer aniosVida;
    BigDecimal pesoPromedio;
    String hierro;                // Marca/identificación
    TipoGanado tipoGanado;        // VACUNO, EQUINO
}

enum CategoriaAnimal {
    TERNERO, TERNERA, AÑOJO, AÑOJA, TORETE, NOVILLA,
    VACA, BUEY, SEMENTAL, CABALLO, YEGUA, CRIA_MACHO, CRIA_HEMBRA
}

enum TipoGanado { VACUNO, EQUINO, PORCINO, OVINO, CAPRINO }
```

### 1.4 Entidad: PlantacionPermanente
```java
@Entity
public class PlantacionPermanente {
    UUID id;
    ActivoFijoTangible activoBase;  // FK

    // Específico plantaciones
    Integer bloque;
    Integer campo;
    BigDecimal areaHectareas;
    TipoCepa cepa;                // S/Q, R/Q, P/2025, F/Q
    String codigoVariedad;        // "97445", "86503"
    Integer aniosCepa;
    TipoPlantacion tipo;          // CAÑA, PLATANO, FRUTAL
}

enum TipoCepa {
    SIEMBRA_QUEDADA,    // S/Q
    RETOÑO_QUEDADO,     // R/Q
    PRIMAVERA_QUEDADA,  // P/Q
    FRIO_QUEDADO,       // F/Q
    SIEMBRA_2025,       // S/2025
    RETOÑO_2025,        // R/2025
    PRIMAVERA_2025      // P/2025
}

enum TipoPlantacion { CAÑA, PLATANO, MANGO, GUAYABA, OTROS_FRUTALES }
```

---

## Fase 2: Depreciación Automática

### 2.1 Entidad: MovimientoDepreciacion
```java
@Entity
public class MovimientoDepreciacion {
    UUID id;
    ActivoFijoTangible activo;
    LocalDate fecha;
    Integer mes;
    Integer anio;
    BigDecimal montoDepreciacion;
    BigDecimal depreciacionAcumuladaAnterior;
    BigDecimal depreciacionAcumuladaNueva;
    BigDecimal valorResidualResultante;
    String observacion;
}
```

### 2.2 Servicio: DepreciacionService
```java
public interface IDepreciacionService {
    // Calcular depreciación mensual para un activo
    BigDecimal calcularDepreciacionMensual(ActivoFijoTangible activo);

    // Ejecutar cierre de depreciación mensual
    void ejecutarCierreMensual(int mes, int anio);

    // Obtener reporte de depreciación
    List<ReporteDepreciacionDto> generarReporteDepreciacion(int anio);
}
```

### Fórmulas según NCC No. 7:
```
Depreciación Anual = (Valor Adquisición - Valor Residual Esperado) × Tasa%
Depreciación Mensual = Depreciación Anual / 12
Valor Residual = Valor Adquisición - Depreciación Acumulada
```

---

## Fase 3: Estructura de Paquetes

```
com.kynsoft.report/
├── domain/
│   ├── dto/
│   │   ├── GrupoActivoFijoDto.java
│   │   ├── ActivoFijoTangibleDto.java
│   │   ├── ActivoAnimalDto.java
│   │   ├── PlantacionPermanenteDto.java
│   │   └── MovimientoDepreciacionDto.java
│   └── services/
│       ├── IGrupoActivoFijoService.java
│       ├── IActivoFijoTangibleService.java
│       ├── IActivoAnimalService.java
│       ├── IPlantacionPermanenteService.java
│       └── IDepreciacionService.java
├── infrastructure/
│   ├── entity/
│   │   ├── GrupoActivoFijo.java
│   │   ├── ActivoFijoTangible.java
│   │   ├── ActivoAnimal.java
│   │   ├── PlantacionPermanente.java
│   │   └── MovimientoDepreciacion.java
│   ├── repository/
│   │   ├── command/
│   │   └── query/
│   └── services/
│       └── (implementaciones)
├── controller/
│   ├── GrupoActivoFijoController.java
│   ├── ActivoFijoTangibleController.java
│   ├── ActivoAnimalController.java
│   ├── PlantacionPermanenteController.java
│   └── DepreciacionController.java
└── applications/
    ├── command/
    │   └── activofijo/
    └── query/
        └── activofijo/
```

---

## Fase 4: Frontend Vue

### 4.1 Componentes:
```
src/components/
├── activosfijos/
│   ├── GrupoActivoFijoList.vue
│   ├── ActivoFijoList.vue          // Lista general con filtros por grupo
│   ├── ActivoFijoForm.vue          // Formulario CRUD
│   ├── ActivoAnimalList.vue        // Vista específica animales
│   ├── PlantacionList.vue          // Vista específica plantaciones
│   └── DepreciacionReporte.vue     // Reporte de depreciación
```

### 4.2 Vistas principales:
- **Inventario AFT**: Lista con filtros por grupo, búsqueda, exportación
- **Animales**: Vista detallada del ganado con categorías
- **Plantaciones**: Vista por bloques/campos con mapas de área
- **Depreciación**: Reporte mensual/anual, cierre contable

---

## Fase 5: Migraciones SQL

### V7__create_activos_fijos.sql
```sql
-- Grupos de activos fijos
CREATE TABLE grupo_activo_fijo (
    id UUID PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    tasa_depreciacion DECIMAL(5,2),
    vida_util_anios INTEGER,
    activo BOOLEAN DEFAULT TRUE
);

-- Datos iniciales según contabilidad cubana
INSERT INTO grupo_activo_fijo (id, codigo, nombre, tasa_depreciacion, vida_util_anios) VALUES
    (gen_random_uuid(), '01', 'Edificios', 2.5, 40),
    (gen_random_uuid(), '02', 'Otras Construcciones', 5.0, 20),
    (gen_random_uuid(), '04', 'Máquinas y Equipos Productivos', 10.0, 10),
    (gen_random_uuid(), '05', 'Aparatos', 20.0, 5),
    (gen_random_uuid(), '07', 'Muebles y Otros Objetos', 10.0, 10),
    (gen_random_uuid(), '08', 'Animales', NULL, NULL),
    (gen_random_uuid(), '12', 'Plantaciones de Caña', NULL, NULL),
    (gen_random_uuid(), '13', 'Plantaciones Permanentes', NULL, NULL);

-- Activos fijos tangibles (tabla base)
CREATE TABLE activo_fijo_tangible (
    id UUID PRIMARY KEY,
    numero_inventario VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    grupo_id UUID REFERENCES grupo_activo_fijo(id),
    finca_id UUID REFERENCES finca(id),
    valor_adquisicion DECIMAL(15,2) NOT NULL,
    depreciacion_acumulada DECIMAL(15,2) DEFAULT 0,
    valor_residual DECIMAL(15,2),
    estado_tecnico_porcentaje INTEGER,
    valor_tasacion DECIMAL(15,2),
    fecha_adquisicion DATE,
    fecha_baja DATE,
    destino VARCHAR(50),
    observaciones TEXT,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Animales (extensión de AFT)
CREATE TABLE activo_animal (
    id UUID PRIMARY KEY,
    activo_fijo_id UUID REFERENCES activo_fijo_tangible(id),
    categoria VARCHAR(50) NOT NULL,
    tipo_ganado VARCHAR(30) NOT NULL,
    anios_vida INTEGER,
    peso_promedio DECIMAL(10,2),
    hierro VARCHAR(50)
);

-- Plantaciones permanentes
CREATE TABLE plantacion_permanente (
    id UUID PRIMARY KEY,
    activo_fijo_id UUID REFERENCES activo_fijo_tangible(id),
    bloque INTEGER,
    campo INTEGER,
    area_hectareas DECIMAL(10,2),
    tipo_cepa VARCHAR(30),
    codigo_variedad VARCHAR(20),
    anios_cepa INTEGER,
    tipo_plantacion VARCHAR(30)
);

-- Movimientos de depreciación
CREATE TABLE movimiento_depreciacion (
    id UUID PRIMARY KEY,
    activo_fijo_id UUID REFERENCES activo_fijo_tangible(id),
    fecha DATE NOT NULL,
    mes INTEGER NOT NULL,
    anio INTEGER NOT NULL,
    monto_depreciacion DECIMAL(15,2) NOT NULL,
    depreciacion_acumulada_anterior DECIMAL(15,2),
    depreciacion_acumulada_nueva DECIMAL(15,2),
    valor_residual_resultante DECIMAL(15,2),
    observacion TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_aft_grupo ON activo_fijo_tangible(grupo_id);
CREATE INDEX idx_aft_finca ON activo_fijo_tangible(finca_id);
CREATE INDEX idx_aft_numero ON activo_fijo_tangible(numero_inventario);
CREATE INDEX idx_depreciacion_fecha ON movimiento_depreciacion(anio, mes);
```

---

## Fase 6: Importación desde Excel

### Servicio de importación:
```java
public interface IImportacionAFTService {
    ImportResultDto importarDesdeExcel(MultipartFile archivo);
    List<ErrorImportacionDto> validarDatos(List<ActivoFijoTangibleDto> datos);
}
```

### Mapeo de columnas Excel → Entidad:
| Excel | Campo Entidad |
|-------|---------------|
| No Inv. | numeroInventario |
| Descripcion | descripcion |
| Valor Adq | valorAdquisicion |
| Dep/Repos | depreciacionAcumulada |
| Valor Resid | valorResidual |
| Est.Tecn (%) | estadoTecnicoPorcentaje |
| Valor Venta | valorTasacion |
| DESTINO | destino |

---

## Cronograma de Implementación

| Fase | Descripción | Prioridad |
|------|-------------|-----------|
| 1 | Modelo de datos (entidades, DTOs) | ALTA |
| 2 | Migraciones SQL | ALTA |
| 3 | Servicios CRUD básicos | ALTA |
| 4 | Controllers REST | ALTA |
| 5 | Frontend - Listas y formularios | MEDIA |
| 6 | Servicio de depreciación | MEDIA |
| 7 | Importación Excel | MEDIA |
| 8 | Reportes PDF | BAJA |

---

## Fuentes Normativas

- [NCC No. 7 - Gaceta Oficial Cuba](https://www.gacetaoficial.gob.cu/es/normas-cubanas-de-informaci%C3%B3n-financiera)
- [Resolución 1038/2017 MFP](https://www.mfp.gob.cu/ficheros/disposiciones/RES-1038-17.pdf)
- [Control de AFT en Cuba - EUMED](https://www.eumed.net/cursecon/ecolat/cu/2012/cbg.html)
