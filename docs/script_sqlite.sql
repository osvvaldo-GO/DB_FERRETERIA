-- ============================================================
-- SCRIPT SQLite — Ferretería Rodamientos y Fierros
-- Herramienta: DB Browser for SQLite
-- Archivo BD:  ferreteria_rodamientos.db
-- Fecha:       Noviembre 2026
-- ============================================================

PRAGMA foreign_keys = ON;

-- Tabla: clientes
CREATE TABLE clientes (
    codigo_cliente  INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre          TEXT    NOT NULL,
    direccion       TEXT    NOT NULL,
    telefono        TEXT    NOT NULL
);

-- Tabla: productos
CREATE TABLE productos (
    codigo_producto INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_producto TEXT    NOT NULL,
    fabricante      TEXT    NOT NULL,
    valor           REAL    NOT NULL DEFAULT 0.0
);

-- Tabla: pedidos (FK → clientes)
CREATE TABLE pedidos (
    codigo_pedido   INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo_cliente  INTEGER NOT NULL,
    descripcion     TEXT    NOT NULL,
    fecha           TEXT    NOT NULL,
    FOREIGN KEY (codigo_cliente)
        REFERENCES clientes(codigo_cliente)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabla: facturas (FK → pedidos)
CREATE TABLE facturas (
    codigo_factura  INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo_pedido   INTEGER NOT NULL,
    fecha_factura   TEXT    NOT NULL,
    valor_total     REAL    NOT NULL DEFAULT 0.0,
    FOREIGN KEY (codigo_pedido)
        REFERENCES pedidos(codigo_pedido)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabla intermedia: detalle_pedido (N:M Pedidos <-> Productos)
CREATE TABLE detalle_pedido (
    id_detalle      INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo_pedido   INTEGER NOT NULL,
    codigo_producto INTEGER NOT NULL,
    cantidad        INTEGER NOT NULL DEFAULT 1,
    precio_unitario REAL    NOT NULL DEFAULT 0.0,
    FOREIGN KEY (codigo_pedido)
        REFERENCES pedidos(codigo_pedido)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (codigo_producto)
        REFERENCES productos(codigo_producto)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Tabla intermedia: detalle_factura (N:M Facturas <-> Productos)
CREATE TABLE detalle_factura (
    id_detalle_fac  INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo_factura  INTEGER NOT NULL,
    codigo_producto INTEGER NOT NULL,
    cantidad        INTEGER NOT NULL DEFAULT 1,
    subtotal        REAL    NOT NULL DEFAULT 0.0,
    FOREIGN KEY (codigo_factura)
        REFERENCES facturas(codigo_factura)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (codigo_producto)
        REFERENCES productos(codigo_producto)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- ── Datos de prueba ──────────────────────────────────────────

INSERT INTO clientes (nombre, direccion, telefono) VALUES
    ('Carlos Ramírez',  'Calle 45 # 12-30, Bogotá',    '3101234567'),
    ('María Gómez',     'Carrera 8 # 22-15, Medellín',  '3209876543'),
    ('Pedro Sánchez',   'Av. 68 # 5-90, Cali',          '3155551234');

INSERT INTO productos (nombre_producto, fabricante, valor) VALUES
    ('Rodamiento 6205',    'SKF',               35000.00),
    ('Tornillo 1/2"',      'Hilti',              1200.00),
    ('Tuerca Hexagonal',   'Hilti',               800.00),
    ('Fierro Liso 1/2"',   'Aceros del Valle',  28000.00),
    ('Llave Inglesa 12"',  'Stanley',           45000.00);

INSERT INTO pedidos (codigo_cliente, descripcion, fecha) VALUES
    (1, 'Pedido de rodamientos y tornillos',  '2026-11-01'),
    (2, 'Pedido de material de construcción', '2026-11-03'),
    (3, 'Pedido de herramientas varias',      '2026-11-05');

INSERT INTO facturas (codigo_pedido, fecha_factura, valor_total) VALUES
    (1, '2026-11-01', 108400.00),
    (2, '2026-11-03',  56000.00),
    (3, '2026-11-05',  45000.00);

INSERT INTO detalle_pedido
    (codigo_pedido, codigo_producto, cantidad, precio_unitario) VALUES
    (1, 1, 2, 35000.00),
    (1, 2, 3,  1200.00),
    (2, 4, 2, 28000.00),
    (3, 5, 1, 45000.00);

INSERT INTO detalle_factura
    (codigo_factura, codigo_producto, cantidad, subtotal) VALUES
    (1, 1, 2, 70000.00),
    (1, 2, 3,  3600.00),
    (2, 4, 2, 56000.00),
    (3, 5, 1, 45000.00);
