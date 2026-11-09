-- ============================================================
-- SCRIPT SQL Server — Ferretería Rodamientos y Fierros
-- Herramienta: SQL Server Management Studio (SSMS)
-- Servidor:    DESKTOP-FK91EFH (SQL Server 16.0)
-- Base datos:  Ferreteria_Rodamientos
-- Fecha:       Noviembre 2026
-- ============================================================

-- Crear la base de datos
CREATE DATABASE Ferreteria_Rodamientos;
GO

USE Ferreteria_Rodamientos;
GO

-- Tabla: clientes
CREATE TABLE clientes (
    codigo_cliente  INT           PRIMARY KEY IDENTITY(1,1),
    nombre          VARCHAR(100)  NOT NULL,
    direccion       VARCHAR(200)  NOT NULL,
    telefono        VARCHAR(20)   NOT NULL
);

-- Tabla: productos
CREATE TABLE productos (
    codigo_producto INT           PRIMARY KEY IDENTITY(1,1),
    nombre_producto VARCHAR(100)  NOT NULL,
    fabricante      VARCHAR(100)  NOT NULL,
    valor           DECIMAL(10,2) NOT NULL DEFAULT 0.00
);

-- Tabla: pedidos (FK → clientes)
CREATE TABLE pedidos (
    codigo_pedido   INT           PRIMARY KEY IDENTITY(1,1),
    codigo_cliente  INT           NOT NULL,
    descripcion     VARCHAR(300)  NOT NULL,
    fecha           DATE          NOT NULL,
    CONSTRAINT FK_pedidos_clientes
        FOREIGN KEY (codigo_cliente)
        REFERENCES clientes(codigo_cliente)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabla: facturas (FK → pedidos)
CREATE TABLE facturas (
    codigo_factura  INT           PRIMARY KEY IDENTITY(1,1),
    codigo_pedido   INT           NOT NULL,
    fecha_factura   DATE          NOT NULL,
    valor_total     DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT FK_facturas_pedidos
        FOREIGN KEY (codigo_pedido)
        REFERENCES pedidos(codigo_pedido)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabla intermedia: detalle_pedido (N:M Pedidos <-> Productos)
CREATE TABLE detalle_pedido (
    id_detalle      INT           PRIMARY KEY IDENTITY(1,1),
    codigo_pedido   INT           NOT NULL,
    codigo_producto INT           NOT NULL,
    cantidad        INT           NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT FK_detped_pedidos
        FOREIGN KEY (codigo_pedido)
        REFERENCES pedidos(codigo_pedido)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_detped_productos
        FOREIGN KEY (codigo_producto)
        REFERENCES productos(codigo_producto)
        ON DELETE NO ACTION ON UPDATE CASCADE
);

-- Tabla intermedia: detalle_factura (N:M Facturas <-> Productos)
CREATE TABLE detalle_factura (
    id_detalle_fac  INT           PRIMARY KEY IDENTITY(1,1),
    codigo_factura  INT           NOT NULL,
    codigo_producto INT           NOT NULL,
    cantidad        INT           NOT NULL DEFAULT 1,
    subtotal        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT FK_detfac_facturas
        FOREIGN KEY (codigo_factura)
        REFERENCES facturas(codigo_factura)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_detfac_productos
        FOREIGN KEY (codigo_producto)
        REFERENCES productos(codigo_producto)
        ON DELETE NO ACTION ON UPDATE CASCADE
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

-- ── Consultas de verificación ────────────────────────────────

SELECT * FROM clientes;
SELECT * FROM productos;
SELECT * FROM pedidos;
SELECT * FROM facturas;
SELECT * FROM detalle_pedido;
SELECT * FROM detalle_factura;
