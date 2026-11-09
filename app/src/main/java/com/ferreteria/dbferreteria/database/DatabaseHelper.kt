package com.ferreteria.dbferreteria.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ferreteria.dbferreteria.model.*

/**
 * DatabaseHelper — clase central que gestiona la base de datos SQLite
 * de la Ferretería Rodamientos y Fierros.
 *
 * Tablas:
 *   clientes, productos, pedidos, facturas,
 *   detalle_pedido, detalle_factura
 *
 * Normalización: 3FN
 *   - 1FN: todos los atributos son atómicos y existe PK en cada tabla.
 *   - 2FN: cada atributo no-clave depende de la clave completa.
 *   - 3FN: no existen dependencias transitivas entre atributos no-clave.
 */
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // ──────────────────────────────────────────────────────────────
    //  Constantes globales de la base de datos
    // ──────────────────────────────────────────────────────────────
    companion object {
        const val DATABASE_NAME    = "ferreteria.db"
        const val DATABASE_VERSION = 1

        // ── TABLA: clientes ──────────────────────────────────────
        const val TABLE_CLIENTES        = "clientes"
        const val COL_CLI_ID            = "codigo_cliente"
        const val COL_CLI_NOMBRE        = "nombre"
        const val COL_CLI_DIRECCION     = "direccion"
        const val COL_CLI_TELEFONO      = "telefono"

        // ── TABLA: productos ─────────────────────────────────────
        const val TABLE_PRODUCTOS       = "productos"
        const val COL_PRO_ID            = "codigo_producto"
        const val COL_PRO_NOMBRE        = "nombre_producto"
        const val COL_PRO_FABRICANTE    = "fabricante"
        const val COL_PRO_VALOR         = "valor"

        // ── TABLA: pedidos ───────────────────────────────────────
        const val TABLE_PEDIDOS         = "pedidos"
        const val COL_PED_ID            = "codigo_pedido"
        const val COL_PED_CLI_ID        = "codigo_cliente"   // FK → clientes
        const val COL_PED_DESCRIPCION   = "descripcion"
        const val COL_PED_FECHA         = "fecha"

        // ── TABLA: facturas ──────────────────────────────────────
        const val TABLE_FACTURAS        = "facturas"
        const val COL_FAC_ID            = "codigo_factura"
        const val COL_FAC_PED_ID        = "codigo_pedido"    // FK → pedidos
        const val COL_FAC_FECHA         = "fecha_factura"
        const val COL_FAC_VALOR_TOTAL   = "valor_total"

        // ── TABLA: detalle_pedido ─────────────────────────────────
        const val TABLE_DET_PEDIDO      = "detalle_pedido"
        const val COL_DP_ID             = "id_detalle"
        const val COL_DP_PED_ID         = "codigo_pedido"    // FK → pedidos
        const val COL_DP_PRO_ID         = "codigo_producto"  // FK → productos
        const val COL_DP_CANTIDAD       = "cantidad"
        const val COL_DP_PRECIO_UNIT    = "precio_unitario"

        // ── TABLA: detalle_factura ────────────────────────────────
        const val TABLE_DET_FACTURA     = "detalle_factura"
        const val COL_DF_ID             = "id_detalle_fac"
        const val COL_DF_FAC_ID         = "codigo_factura"   // FK → facturas
        const val COL_DF_PRO_ID         = "codigo_producto"  // FK → productos
        const val COL_DF_CANTIDAD       = "cantidad"
        const val COL_DF_SUBTOTAL       = "subtotal"
    }

    // ──────────────────────────────────────────────────────────────
    //  onCreate — crea todas las tablas al instalar la app
    // ──────────────────────────────────────────────────────────────
    override fun onCreate(db: SQLiteDatabase) {
        // Activar claves foráneas en SQLite
        db.execSQL("PRAGMA foreign_keys = ON;")

        db.execSQL(
            """
            CREATE TABLE $TABLE_CLIENTES (
                $COL_CLI_ID       INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CLI_NOMBRE   TEXT    NOT NULL,
                $COL_CLI_DIRECCION TEXT   NOT NULL,
                $COL_CLI_TELEFONO TEXT    NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_PRODUCTOS (
                $COL_PRO_ID         INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PRO_NOMBRE     TEXT    NOT NULL,
                $COL_PRO_FABRICANTE TEXT    NOT NULL,
                $COL_PRO_VALOR      REAL    NOT NULL DEFAULT 0.0
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_PEDIDOS (
                $COL_PED_ID          INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PED_CLI_ID      INTEGER NOT NULL,
                $COL_PED_DESCRIPCION TEXT    NOT NULL,
                $COL_PED_FECHA       TEXT    NOT NULL,
                FOREIGN KEY ($COL_PED_CLI_ID) REFERENCES $TABLE_CLIENTES($COL_CLI_ID)
                    ON DELETE CASCADE ON UPDATE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_FACTURAS (
                $COL_FAC_ID          INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_FAC_PED_ID      INTEGER NOT NULL,
                $COL_FAC_FECHA       TEXT    NOT NULL,
                $COL_FAC_VALOR_TOTAL REAL    NOT NULL DEFAULT 0.0,
                FOREIGN KEY ($COL_FAC_PED_ID) REFERENCES $TABLE_PEDIDOS($COL_PED_ID)
                    ON DELETE CASCADE ON UPDATE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_DET_PEDIDO (
                $COL_DP_ID          INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DP_PED_ID      INTEGER NOT NULL,
                $COL_DP_PRO_ID      INTEGER NOT NULL,
                $COL_DP_CANTIDAD    INTEGER NOT NULL DEFAULT 1,
                $COL_DP_PRECIO_UNIT REAL    NOT NULL DEFAULT 0.0,
                FOREIGN KEY ($COL_DP_PED_ID) REFERENCES $TABLE_PEDIDOS($COL_PED_ID)
                    ON DELETE CASCADE ON UPDATE CASCADE,
                FOREIGN KEY ($COL_DP_PRO_ID) REFERENCES $TABLE_PRODUCTOS($COL_PRO_ID)
                    ON DELETE RESTRICT ON UPDATE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_DET_FACTURA (
                $COL_DF_ID       INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DF_FAC_ID   INTEGER NOT NULL,
                $COL_DF_PRO_ID   INTEGER NOT NULL,
                $COL_DF_CANTIDAD INTEGER NOT NULL DEFAULT 1,
                $COL_DF_SUBTOTAL REAL    NOT NULL DEFAULT 0.0,
                FOREIGN KEY ($COL_DF_FAC_ID) REFERENCES $TABLE_FACTURAS($COL_FAC_ID)
                    ON DELETE CASCADE ON UPDATE CASCADE,
                FOREIGN KEY ($COL_DF_PRO_ID) REFERENCES $TABLE_PRODUCTOS($COL_PRO_ID)
                    ON DELETE RESTRICT ON UPDATE CASCADE
            )
            """.trimIndent()
        )
    }

    // ──────────────────────────────────────────────────────────────
    //  onUpgrade — descarta y recrea las tablas al cambiar versión
    // ──────────────────────────────────────────────────────────────
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Eliminar en orden inverso para respetar FKs
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DET_FACTURA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DET_PEDIDO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FACTURAS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PEDIDOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENTES")
        onCreate(db)
    }

    // ──────────────────────────────────────────────────────────────
    //  Activar FKs cada vez que se abre la conexión
    // ──────────────────────────────────────────────────────────────
    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        if (!db.isReadOnly) {
            db.execSQL("PRAGMA foreign_keys = ON;")
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  CRUD — CLIENTES
    // ══════════════════════════════════════════════════════════════

    /** Inserta un cliente. Devuelve el rowId o -1 si falla. */
    fun insertarCliente(nombre: String, direccion: String, telefono: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_CLI_NOMBRE,    nombre)
            put(COL_CLI_DIRECCION, direccion)
            put(COL_CLI_TELEFONO,  telefono)
        }
        return db.insert(TABLE_CLIENTES, null, values)
    }

    /** Devuelve la lista completa de clientes. */
    fun obtenerClientes(): List<Cliente> {
        val lista = mutableListOf<Cliente>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CLIENTES ORDER BY $COL_CLI_NOMBRE", null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    lista.add(
                        Cliente(
                            id        = it.getInt(it.getColumnIndexOrThrow(COL_CLI_ID)),
                            nombre    = it.getString(it.getColumnIndexOrThrow(COL_CLI_NOMBRE)),
                            direccion = it.getString(it.getColumnIndexOrThrow(COL_CLI_DIRECCION)),
                            telefono  = it.getString(it.getColumnIndexOrThrow(COL_CLI_TELEFONO))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return lista
    }

    /** Devuelve un cliente por su ID, o null si no existe. */
    fun obtenerClientePorId(id: Int): Cliente? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CLIENTES, null,
            "$COL_CLI_ID = ?", arrayOf(id.toString()),
            null, null, null
        )
        return cursor.use {
            if (it.moveToFirst()) {
                Cliente(
                    id        = it.getInt(it.getColumnIndexOrThrow(COL_CLI_ID)),
                    nombre    = it.getString(it.getColumnIndexOrThrow(COL_CLI_NOMBRE)),
                    direccion = it.getString(it.getColumnIndexOrThrow(COL_CLI_DIRECCION)),
                    telefono  = it.getString(it.getColumnIndexOrThrow(COL_CLI_TELEFONO))
                )
            } else null
        }
    }

    /** Actualiza los datos de un cliente. Devuelve las filas afectadas. */
    fun actualizarCliente(id: Int, nombre: String, direccion: String, telefono: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_CLI_NOMBRE,    nombre)
            put(COL_CLI_DIRECCION, direccion)
            put(COL_CLI_TELEFONO,  telefono)
        }
        return db.update(TABLE_CLIENTES, values, "$COL_CLI_ID = ?", arrayOf(id.toString()))
    }

    /** Elimina un cliente por su ID. Devuelve las filas afectadas. */
    fun eliminarCliente(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_CLIENTES, "$COL_CLI_ID = ?", arrayOf(id.toString()))
    }

    // ══════════════════════════════════════════════════════════════
    //  CRUD — PRODUCTOS
    // ══════════════════════════════════════════════════════════════

    fun insertarProducto(nombre: String, fabricante: String, valor: Double): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_PRO_NOMBRE,     nombre)
            put(COL_PRO_FABRICANTE, fabricante)
            put(COL_PRO_VALOR,      valor)
        }
        return db.insert(TABLE_PRODUCTOS, null, values)
    }

    fun obtenerProductos(): List<Producto> {
        val lista = mutableListOf<Producto>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PRODUCTOS ORDER BY $COL_PRO_NOMBRE", null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    lista.add(
                        Producto(
                            id         = it.getInt(it.getColumnIndexOrThrow(COL_PRO_ID)),
                            nombre     = it.getString(it.getColumnIndexOrThrow(COL_PRO_NOMBRE)),
                            fabricante = it.getString(it.getColumnIndexOrThrow(COL_PRO_FABRICANTE)),
                            valor      = it.getDouble(it.getColumnIndexOrThrow(COL_PRO_VALOR))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return lista
    }

    fun obtenerProductoPorId(id: Int): Producto? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTOS, null,
            "$COL_PRO_ID = ?", arrayOf(id.toString()),
            null, null, null
        )
        return cursor.use {
            if (it.moveToFirst()) {
                Producto(
                    id         = it.getInt(it.getColumnIndexOrThrow(COL_PRO_ID)),
                    nombre     = it.getString(it.getColumnIndexOrThrow(COL_PRO_NOMBRE)),
                    fabricante = it.getString(it.getColumnIndexOrThrow(COL_PRO_FABRICANTE)),
                    valor      = it.getDouble(it.getColumnIndexOrThrow(COL_PRO_VALOR))
                )
            } else null
        }
    }

    fun actualizarProducto(id: Int, nombre: String, fabricante: String, valor: Double): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_PRO_NOMBRE,     nombre)
            put(COL_PRO_FABRICANTE, fabricante)
            put(COL_PRO_VALOR,      valor)
        }
        return db.update(TABLE_PRODUCTOS, values, "$COL_PRO_ID = ?", arrayOf(id.toString()))
    }

    fun eliminarProducto(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_PRODUCTOS, "$COL_PRO_ID = ?", arrayOf(id.toString()))
    }

    // ══════════════════════════════════════════════════════════════
    //  CRUD — PEDIDOS
    // ══════════════════════════════════════════════════════════════

    fun insertarPedido(codigoCliente: Int, descripcion: String, fecha: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_PED_CLI_ID,      codigoCliente)
            put(COL_PED_DESCRIPCION, descripcion)
            put(COL_PED_FECHA,       fecha)
        }
        return db.insert(TABLE_PEDIDOS, null, values)
    }

    fun obtenerPedidos(): List<Pedido> {
        val lista = mutableListOf<Pedido>()
        val db = readableDatabase
        // JOIN para obtener el nombre del cliente
        val sql = """
            SELECT p.$COL_PED_ID, p.$COL_PED_CLI_ID, c.$COL_CLI_NOMBRE AS nombre_cliente,
                   p.$COL_PED_DESCRIPCION, p.$COL_PED_FECHA
            FROM $TABLE_PEDIDOS p
            INNER JOIN $TABLE_CLIENTES c ON p.$COL_PED_CLI_ID = c.$COL_CLI_ID
            ORDER BY p.$COL_PED_FECHA DESC
        """.trimIndent()
        val cursor = db.rawQuery(sql, null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    lista.add(
                        Pedido(
                            id             = it.getInt(it.getColumnIndexOrThrow(COL_PED_ID)),
                            codigoCliente  = it.getInt(it.getColumnIndexOrThrow(COL_PED_CLI_ID)),
                            nombreCliente  = it.getString(it.getColumnIndexOrThrow("nombre_cliente")),
                            descripcion    = it.getString(it.getColumnIndexOrThrow(COL_PED_DESCRIPCION)),
                            fecha          = it.getString(it.getColumnIndexOrThrow(COL_PED_FECHA))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return lista
    }

    fun obtenerPedidoPorId(id: Int): Pedido? {
        val db = readableDatabase
        val sql = """
            SELECT p.$COL_PED_ID, p.$COL_PED_CLI_ID, c.$COL_CLI_NOMBRE AS nombre_cliente,
                   p.$COL_PED_DESCRIPCION, p.$COL_PED_FECHA
            FROM $TABLE_PEDIDOS p
            INNER JOIN $TABLE_CLIENTES c ON p.$COL_PED_CLI_ID = c.$COL_CLI_ID
            WHERE p.$COL_PED_ID = ?
        """.trimIndent()
        val cursor = db.rawQuery(sql, arrayOf(id.toString()))
        return cursor.use {
            if (it.moveToFirst()) {
                Pedido(
                    id            = it.getInt(it.getColumnIndexOrThrow(COL_PED_ID)),
                    codigoCliente = it.getInt(it.getColumnIndexOrThrow(COL_PED_CLI_ID)),
                    nombreCliente = it.getString(it.getColumnIndexOrThrow("nombre_cliente")),
                    descripcion   = it.getString(it.getColumnIndexOrThrow(COL_PED_DESCRIPCION)),
                    fecha         = it.getString(it.getColumnIndexOrThrow(COL_PED_FECHA))
                )
            } else null
        }
    }

    fun actualizarPedido(id: Int, codigoCliente: Int, descripcion: String, fecha: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_PED_CLI_ID,      codigoCliente)
            put(COL_PED_DESCRIPCION, descripcion)
            put(COL_PED_FECHA,       fecha)
        }
        return db.update(TABLE_PEDIDOS, values, "$COL_PED_ID = ?", arrayOf(id.toString()))
    }

    fun eliminarPedido(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_PEDIDOS, "$COL_PED_ID = ?", arrayOf(id.toString()))
    }

    // ══════════════════════════════════════════════════════════════
    //  CRUD — FACTURAS
    // ══════════════════════════════════════════════════════════════

    fun insertarFactura(codigoPedido: Int, fecha: String, valorTotal: Double): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_FAC_PED_ID,      codigoPedido)
            put(COL_FAC_FECHA,       fecha)
            put(COL_FAC_VALOR_TOTAL, valorTotal)
        }
        return db.insert(TABLE_FACTURAS, null, values)
    }

    fun obtenerFacturas(): List<Factura> {
        val lista = mutableListOf<Factura>()
        val db = readableDatabase
        val sql = """
            SELECT f.$COL_FAC_ID, f.$COL_FAC_PED_ID,
                   p.$COL_PED_DESCRIPCION AS desc_pedido,
                   f.$COL_FAC_FECHA, f.$COL_FAC_VALOR_TOTAL
            FROM $TABLE_FACTURAS f
            INNER JOIN $TABLE_PEDIDOS p ON f.$COL_FAC_PED_ID = p.$COL_PED_ID
            ORDER BY f.$COL_FAC_FECHA DESC
        """.trimIndent()
        val cursor = db.rawQuery(sql, null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    lista.add(
                        Factura(
                            id           = it.getInt(it.getColumnIndexOrThrow(COL_FAC_ID)),
                            codigoPedido = it.getInt(it.getColumnIndexOrThrow(COL_FAC_PED_ID)),
                            descPedido   = it.getString(it.getColumnIndexOrThrow("desc_pedido")),
                            fecha        = it.getString(it.getColumnIndexOrThrow(COL_FAC_FECHA)),
                            valorTotal   = it.getDouble(it.getColumnIndexOrThrow(COL_FAC_VALOR_TOTAL))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return lista
    }

    fun obtenerFacturaPorId(id: Int): Factura? {
        val db = readableDatabase
        val sql = """
            SELECT f.$COL_FAC_ID, f.$COL_FAC_PED_ID,
                   p.$COL_PED_DESCRIPCION AS desc_pedido,
                   f.$COL_FAC_FECHA, f.$COL_FAC_VALOR_TOTAL
            FROM $TABLE_FACTURAS f
            INNER JOIN $TABLE_PEDIDOS p ON f.$COL_FAC_PED_ID = p.$COL_PED_ID
            WHERE f.$COL_FAC_ID = ?
        """.trimIndent()
        val cursor = db.rawQuery(sql, arrayOf(id.toString()))
        return cursor.use {
            if (it.moveToFirst()) {
                Factura(
                    id           = it.getInt(it.getColumnIndexOrThrow(COL_FAC_ID)),
                    codigoPedido = it.getInt(it.getColumnIndexOrThrow(COL_FAC_PED_ID)),
                    descPedido   = it.getString(it.getColumnIndexOrThrow("desc_pedido")),
                    fecha        = it.getString(it.getColumnIndexOrThrow(COL_FAC_FECHA)),
                    valorTotal   = it.getDouble(it.getColumnIndexOrThrow(COL_FAC_VALOR_TOTAL))
                )
            } else null
        }
    }

    fun actualizarFactura(id: Int, codigoPedido: Int, fecha: String, valorTotal: Double): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_FAC_PED_ID,      codigoPedido)
            put(COL_FAC_FECHA,       fecha)
            put(COL_FAC_VALOR_TOTAL, valorTotal)
        }
        return db.update(TABLE_FACTURAS, values, "$COL_FAC_ID = ?", arrayOf(id.toString()))
    }

    fun eliminarFactura(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_FACTURAS, "$COL_FAC_ID = ?", arrayOf(id.toString()))
    }

    // ══════════════════════════════════════════════════════════════
    //  CRUD — DETALLE_PEDIDO
    // ══════════════════════════════════════════════════════════════

    fun insertarDetallePedido(codigoPedido: Int, codigoProducto: Int,
                               cantidad: Int, precioUnitario: Double): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_DP_PED_ID,      codigoPedido)
            put(COL_DP_PRO_ID,      codigoProducto)
            put(COL_DP_CANTIDAD,    cantidad)
            put(COL_DP_PRECIO_UNIT, precioUnitario)
        }
        return db.insert(TABLE_DET_PEDIDO, null, values)
    }

    fun obtenerDetallesPorPedido(codigoPedido: Int): List<DetallePedido> {
        val lista = mutableListOf<DetallePedido>()
        val db = readableDatabase
        val sql = """
            SELECT dp.$COL_DP_ID, dp.$COL_DP_PED_ID, dp.$COL_DP_PRO_ID,
                   pr.$COL_PRO_NOMBRE AS nombre_producto,
                   dp.$COL_DP_CANTIDAD, dp.$COL_DP_PRECIO_UNIT
            FROM $TABLE_DET_PEDIDO dp
            INNER JOIN $TABLE_PRODUCTOS pr ON dp.$COL_DP_PRO_ID = pr.$COL_PRO_ID
            WHERE dp.$COL_DP_PED_ID = ?
        """.trimIndent()
        val cursor = db.rawQuery(sql, arrayOf(codigoPedido.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    lista.add(
                        DetallePedido(
                            id              = it.getInt(it.getColumnIndexOrThrow(COL_DP_ID)),
                            codigoPedido    = it.getInt(it.getColumnIndexOrThrow(COL_DP_PED_ID)),
                            codigoProducto  = it.getInt(it.getColumnIndexOrThrow(COL_DP_PRO_ID)),
                            nombreProducto  = it.getString(it.getColumnIndexOrThrow("nombre_producto")),
                            cantidad        = it.getInt(it.getColumnIndexOrThrow(COL_DP_CANTIDAD)),
                            precioUnitario  = it.getDouble(it.getColumnIndexOrThrow(COL_DP_PRECIO_UNIT))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return lista
    }

    fun eliminarDetallePedido(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_DET_PEDIDO, "$COL_DP_ID = ?", arrayOf(id.toString()))
    }

    // ══════════════════════════════════════════════════════════════
    //  CRUD — DETALLE_FACTURA
    // ══════════════════════════════════════════════════════════════

    fun insertarDetalleFactura(codigoFactura: Int, codigoProducto: Int,
                                cantidad: Int, subtotal: Double): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_DF_FAC_ID,   codigoFactura)
            put(COL_DF_PRO_ID,   codigoProducto)
            put(COL_DF_CANTIDAD, cantidad)
            put(COL_DF_SUBTOTAL, subtotal)
        }
        return db.insert(TABLE_DET_FACTURA, null, values)
    }

    fun obtenerDetallesPorFactura(codigoFactura: Int): List<DetalleFactura> {
        val lista = mutableListOf<DetalleFactura>()
        val db = readableDatabase
        val sql = """
            SELECT df.$COL_DF_ID, df.$COL_DF_FAC_ID, df.$COL_DF_PRO_ID,
                   pr.$COL_PRO_NOMBRE AS nombre_producto,
                   df.$COL_DF_CANTIDAD, df.$COL_DF_SUBTOTAL
            FROM $TABLE_DET_FACTURA df
            INNER JOIN $TABLE_PRODUCTOS pr ON df.$COL_DF_PRO_ID = pr.$COL_PRO_ID
            WHERE df.$COL_DF_FAC_ID = ?
        """.trimIndent()
        val cursor = db.rawQuery(sql, arrayOf(codigoFactura.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    lista.add(
                        DetalleFactura(
                            id             = it.getInt(it.getColumnIndexOrThrow(COL_DF_ID)),
                            codigoFactura  = it.getInt(it.getColumnIndexOrThrow(COL_DF_FAC_ID)),
                            codigoProducto = it.getInt(it.getColumnIndexOrThrow(COL_DF_PRO_ID)),
                            nombreProducto = it.getString(it.getColumnIndexOrThrow("nombre_producto")),
                            cantidad       = it.getInt(it.getColumnIndexOrThrow(COL_DF_CANTIDAD)),
                            subtotal       = it.getDouble(it.getColumnIndexOrThrow(COL_DF_SUBTOTAL))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return lista
    }

    fun eliminarDetalleFactura(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_DET_FACTURA, "$COL_DF_ID = ?", arrayOf(id.toString()))
    }
}
