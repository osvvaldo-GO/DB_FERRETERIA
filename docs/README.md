# Ferretería Rodamientos y Fierros
## Documentación del Proyecto — Tarea #1

---

## 1. DESCRIPCIÓN DEL PROYECTO

Aplicación móvil Android desarrollada en **Kotlin** con base de datos **SQLite**,
para la gestión de información de la Ferretería Rodamientos y Fierros.

- **IDE:** Android Studio
- **Lenguaje:** Kotlin
- **Base de datos:** SQLite (via SQLiteOpenHelper)
- **Arquitectura:** Activities + ViewBinding + RecyclerView
- **Package:** com.ferreteria.dbferreteria
- **MinSDK:** 24 (Android 7.0)
- **TargetSDK:** 34 (Android 14)

---

## 2. REQUISITOS DE USUARIO (Entrevista con el Stakeholder)

El cliente (dueño de la Ferretería Rodamientos y Fierros) requiere un sistema
que gestione la siguiente información:

### Clientes
- De cada cliente se desea conocer: código, nombre, dirección y teléfono.

### Productos
- De cada producto se desea conocer: código, nombre, fabricante y valor.

### Pedidos
- Cada cliente puede realizar varios pedidos.
- Un pedido solo lo puede realizar un solo cliente.
- De cada pedido se desea conocer: código, descripción y fecha.

### Facturas
- De cada factura generada por compra de productos se desea conocer:
  código, fecha y valor total.
- Cada factura está asociada a un pedido.

### Relaciones identificadas
- CLIENTE realiza PEDIDOS → relación 1:N
- PEDIDO genera FACTURA → relación 1:1
- PEDIDO contiene PRODUCTOS → relación N:M (tabla DETALLE_PEDIDO)
- FACTURA contiene PRODUCTOS → relación N:M (tabla DETALLE_FACTURA)

---

## 3. MODELO ENTIDAD-RELACIÓN (3FN)

### Tablas resultantes (normalizadas hasta 3FN)

#### CLIENTES
| Campo          | Tipo    | Restricción          |
|----------------|---------|----------------------|
| codigo_cliente | INTEGER | PK, AUTOINCREMENT    |
| nombre         | TEXT    | NOT NULL             |
| direccion      | TEXT    | NOT NULL             |
| telefono       | TEXT    | NOT NULL             |

#### PRODUCTOS
| Campo           | Tipo    | Restricción          |
|-----------------|---------|----------------------|
| codigo_producto | INTEGER | PK, AUTOINCREMENT    |
| nombre_producto | TEXT    | NOT NULL             |
| fabricante      | TEXT    | NOT NULL             |
| valor           | REAL    | NOT NULL DEFAULT 0.0 |

#### PEDIDOS
| Campo          | Tipo    | Restricción                    |
|----------------|---------|--------------------------------|
| codigo_pedido  | INTEGER | PK, AUTOINCREMENT              |
| codigo_cliente | INTEGER | FK → clientes(codigo_cliente)  |
| descripcion    | TEXT    | NOT NULL                       |
| fecha          | TEXT    | NOT NULL                       |

#### FACTURAS
| Campo          | Tipo    | Restricción                   |
|----------------|---------|-------------------------------|
| codigo_factura | INTEGER | PK, AUTOINCREMENT             |
| codigo_pedido  | INTEGER | FK → pedidos(codigo_pedido)   |
| fecha_factura  | TEXT    | NOT NULL                      |
| valor_total    | REAL    | NOT NULL DEFAULT 0.0          |

#### DETALLE_PEDIDO (tabla intermedia N:M)
| Campo           | Tipo    | Restricción                      |
|-----------------|---------|----------------------------------|
| id_detalle      | INTEGER | PK, AUTOINCREMENT                |
| codigo_pedido   | INTEGER | FK → pedidos(codigo_pedido)      |
| codigo_producto | INTEGER | FK → productos(codigo_producto)  |
| cantidad        | INTEGER | NOT NULL DEFAULT 1               |
| precio_unitario | REAL    | NOT NULL DEFAULT 0.0             |

#### DETALLE_FACTURA (tabla intermedia N:M)
| Campo           | Tipo    | Restricción                      |
|-----------------|---------|----------------------------------|
| id_detalle_fac  | INTEGER | PK, AUTOINCREMENT                |
| codigo_factura  | INTEGER | FK → facturas(codigo_factura)    |
| codigo_producto | INTEGER | FK → productos(codigo_producto)  |
| cantidad        | INTEGER | NOT NULL DEFAULT 1               |
| subtotal        | REAL    | NOT NULL DEFAULT 0.0             |

### Normalización aplicada
- **1FN:** Todos los atributos son atómicos, cada tabla tiene PK definida,
  no hay grupos repetitivos.
- **2FN:** Cada atributo no-clave depende de la clave completa.
  Las tablas DETALLE_PEDIDO y DETALLE_FACTURA resuelven las dependencias
  parciales de las relaciones N:M.
- **3FN:** No existen dependencias transitivas. Cada atributo depende
  directamente de su PK.

---

## 4. ESTRUCTURA DEL PROYECTO ANDROID

```
DB_FERRETERIA/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/ferreteria/dbferreteria/
│       │   ├── MainActivity.kt                  ← Menú principal (4 tarjetas)
│       │   ├── adapter/
│       │   │   └── GenericoAdapter.kt           ← Adaptador RecyclerView reutilizable
│       │   ├── database/
│       │   │   └── DatabaseHelper.kt            ← SQLiteOpenHelper + CRUD completo
│       │   ├── model/
│       │   │   └── Modelos.kt                   ← Data classes (6 entidades)
│       │   ├── clientes/
│       │   │   ├── ClientesActivity.kt          ← Lista + búsqueda
│       │   │   └── ClienteFormActivity.kt       ← Crear/Editar
│       │   ├── productos/
│       │   │   ├── ProductosActivity.kt
│       │   │   └── ProductoFormActivity.kt
│       │   ├── pedidos/
│       │   │   ├── PedidosActivity.kt
│       │   │   └── PedidoFormActivity.kt        ← Con Spinner + DatePickerDialog
│       │   └── facturas/
│       │       ├── FacturasActivity.kt
│       │       └── FacturaFormActivity.kt       ← Con Spinner + DatePickerDialog
│       └── res/
│           ├── layout/
│           │   ├── activity_main.xml
│           │   ├── item_lista_generico.xml
│           │   ├── activity_clientes.xml
│           │   ├── activity_cliente_form.xml
│           │   ├── activity_productos.xml
│           │   ├── activity_producto_form.xml
│           │   ├── activity_pedidos.xml
│           │   ├── activity_pedido_form.xml
│           │   ├── activity_facturas.xml
│           │   └── activity_factura_form.xml
│           ├── drawable/
│           │   ├── ic_launcher.xml
│           │   └── spinner_background.xml
│           └── values/
│               ├── strings.xml
│               ├── colors.xml
│               └── themes.xml
├── build.gradle
├── settings.gradle
├── gradle.properties
├── local.properties               ← Apunta al SDK de Android
└── docs/
    ├── README.md                  ← Este archivo
    ├── script_sqlite.sql          ← Script para SQLite (DB Browser)
    └── script_sqlserver.sql       ← Script para SQL Server (SSMS)
```

---


### Requisito del dispositivo
- Android 7.0 o superior (minSdk 24)
- El proyecto fue probado con Xiaomi Redmi Note 9S

---

## 5. BASES DE DATOS CREADAS

### SQLite (para el proyecto Android)
- Archivo: `ferreteria_rodamientos.db`
- Ubicación: Escritorio del PC
- Herramienta: DB Browser for SQLite
- Script: ver `docs/script_sqlite.sql`

### SQL Server (para el diagrama del informe)
- Base de datos: `Ferreteria_Rodamientos`
- Servidor: DESKTOP-FK91EFH (SQL Server 16.0)
- Diagrama: guardado como `Diagram_Ferreteria` en SSMS
- Script: ver `docs/script_sqlserver.sql`

---

## 6. TAREA #2

Los siguientes puntos se desarrollarán en la Tarea #2:

4. Programación de la aplicación a partir de los requisitos
5. Desarrollo y despliegue de aplicaciones móviles
6. Aplicaciones creadas a partir de código propio
7. Creación de menús y ventanas
8. Pruebas unitarias — Despliegue en forma local
9. Despliegue en tienda de aplicaciones
10. Bases de Datos con SQLite (integración completa)

### Nota sobre Pruebas unitarias (punto 8)
La sustentación de estas pruebas se realiza con la evidencia de funcionamiento
de la App mediante capturas de pantalla de los aspectos relevantes de
funcionalidad del software, incluyendo:
- Despliegue exitoso en dispositivo físico **Xiaomi Redmi Note 9S**
- Despliegue exitoso en emulador **Pixel 8** de Android Studio
- Operaciones CRUD verificadas: inserción, consulta, actualización y eliminación
  de registros en las entidades Clientes, Productos, Pedidos y Facturas
- Video de demostración del funcionamiento completo de la aplicación

---

## 8. NOTAS TÉCNICAS IMPORTANTES

- El `local.properties` apunta al SDK en:
  `C:\Users\OSWALDO\AppData\Local\Android\Sdk`
- ViewBinding está habilitado en `app/build.gradle`
- Las Foreign Keys en SQLite requieren `PRAGMA foreign_keys = ON`
  (activado en `onOpen()` del DatabaseHelper)
- El adaptador `GenericoAdapter` es genérico con tipo `<T>` y reutilizable
  para todas las entidades
- Los formularios de Pedido y Factura usan `Spinner` para seleccionar
  entidades relacionadas y `DatePickerDialog` para las fechas
