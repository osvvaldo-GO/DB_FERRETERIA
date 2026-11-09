package com.ferreteria.dbferreteria.model

/**
 * Data classes que representan cada entidad del modelo ER
 * normalizado hasta 3FN de la Ferretería Rodamientos y Fierros.
 */

// ── Entidad principal: CLIENTE ────────────────────────────────────────────
data class Cliente(
    val id: Int = 0,
    val nombre: String,
    val direccion: String,
    val telefono: String
) {
    override fun toString(): String = nombre
}

// ── Entidad principal: PRODUCTO ───────────────────────────────────────────
data class Producto(
    val id: Int = 0,
    val nombre: String,
    val fabricante: String,
    val valor: Double
) {
    override fun toString(): String = "$nombre (\$${"%.2f".format(valor)})"
}

// ── Entidad principal: PEDIDO ─────────────────────────────────────────────
data class Pedido(
    val id: Int = 0,
    val codigoCliente: Int,
    val nombreCliente: String = "",   // resuelto por JOIN
    val descripcion: String,
    val fecha: String
)

// ── Entidad principal: FACTURA ────────────────────────────────────────────
data class Factura(
    val id: Int = 0,
    val codigoPedido: Int,
    val descPedido: String = "",      // resuelto por JOIN
    val fecha: String,
    val valorTotal: Double
)

// ── Entidad intermedia: DETALLE_PEDIDO (resuelve N:M Pedido↔Producto) ─────
data class DetallePedido(
    val id: Int = 0,
    val codigoPedido: Int,
    val codigoProducto: Int,
    val nombreProducto: String = "",  // resuelto por JOIN
    val cantidad: Int,
    val precioUnitario: Double
) {
    val subtotal: Double get() = cantidad * precioUnitario
}

// ── Entidad intermedia: DETALLE_FACTURA (resuelve N:M Factura↔Producto) ───
data class DetalleFactura(
    val id: Int = 0,
    val codigoFactura: Int,
    val codigoProducto: Int,
    val nombreProducto: String = "",  // resuelto por JOIN
    val cantidad: Int,
    val subtotal: Double
)
