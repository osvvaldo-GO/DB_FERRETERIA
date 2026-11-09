package com.ferreteria.dbferreteria.productos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ferreteria.dbferreteria.database.DatabaseHelper
import com.ferreteria.dbferreteria.databinding.ActivityProductoFormBinding

/**
 * ProductoFormActivity — formulario para crear o editar un producto.
 */
class ProductoFormActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PRODUCTO_ID = "producto_id"
    }

    private lateinit var binding: ActivityProductoFormBinding
    private lateinit var dbHelper: DatabaseHelper
    private var productoId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        productoId = intent.getIntExtra(EXTRA_PRODUCTO_ID, -1).takeIf { it != -1 }

        supportActionBar?.apply {
            title = if (productoId == null) "Nuevo Producto" else "Editar Producto"
            setDisplayHomeAsUpEnabled(true)
        }

        productoId?.let { cargarProducto(it) }

        binding.btnGuardar.setOnClickListener  { guardarProducto() }
        binding.btnCancelar.setOnClickListener { finish() }
    }

    private fun cargarProducto(id: Int) {
        dbHelper.obtenerProductoPorId(id)?.let {
            binding.etNombre.setText(it.nombre)
            binding.etFabricante.setText(it.fabricante)
            binding.etValor.setText(it.valor.toString())
        }
    }

    private fun guardarProducto() {
        val nombre     = binding.etNombre.text.toString().trim()
        val fabricante = binding.etFabricante.text.toString().trim()
        val valorStr   = binding.etValor.text.toString().trim()

        if (nombre.isEmpty() || fabricante.isEmpty() || valorStr.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val valor = valorStr.toDoubleOrNull()
        if (valor == null || valor < 0) {
            Toast.makeText(this, "Ingrese un valor numérico válido", Toast.LENGTH_SHORT).show()
            return
        }

        val ok = if (productoId == null) {
            dbHelper.insertarProducto(nombre, fabricante, valor) > 0
        } else {
            dbHelper.actualizarProducto(productoId!!, nombre, fabricante, valor) > 0
        }

        if (ok) {
            Toast.makeText(
                this,
                if (productoId == null) "Producto creado" else "Producto actualizado",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
