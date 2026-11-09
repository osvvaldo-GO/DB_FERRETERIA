package com.ferreteria.dbferreteria.productos

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferreteria.dbferreteria.adapter.GenericoAdapter
import com.ferreteria.dbferreteria.database.DatabaseHelper
import com.ferreteria.dbferreteria.databinding.ActivityProductosBinding
import com.ferreteria.dbferreteria.model.Producto
import com.google.android.material.snackbar.Snackbar

/**
 * ProductosActivity — lista todos los productos con búsqueda y CRUD completo.
 */
class ProductosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductosBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: GenericoAdapter<Producto>

    private var listaMaestra: List<Producto> = emptyList()
    private val listaFiltrada: MutableList<Producto> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Productos"
            setDisplayHomeAsUpEnabled(true)
        }

        dbHelper = DatabaseHelper(this)

        adapter = GenericoAdapter(
            items      = listaFiltrada,
            titulo     = { it.nombre },
            subtitulo  = { "🏭 ${it.fabricante}" },
            detalle    = { "💰 ${"%.2f".format(it.valor)}" },
            onEditar   = { pos -> abrirFormulario(listaFiltrada[pos]) },
            onEliminar = { pos -> confirmarEliminar(listaFiltrada[pos]) }
        )

        binding.recyclerProductos.layoutManager = LinearLayoutManager(this)
        binding.recyclerProductos.adapter = adapter

        binding.fabNuevoProducto.setOnClickListener {
            abrirFormulario(null)
        }

        binding.etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        cargarProductos()
    }

    private fun cargarProductos() {
        listaMaestra = dbHelper.obtenerProductos()
        filtrar(binding.etBuscar.text.toString())
    }

    private fun filtrar(query: String) {
        val resultado = if (query.isBlank()) {
            listaMaestra
        } else {
            listaMaestra.filter {
                it.nombre.contains(query, ignoreCase = true) ||
                it.fabricante.contains(query, ignoreCase = true)
            }
        }

        listaFiltrada.clear()
        listaFiltrada.addAll(resultado)
        adapter.notifyDataSetChanged()

        binding.tvContador.text = "${listaFiltrada.size} registro(s)"
        binding.recyclerProductos.visibility =
            if (listaFiltrada.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE
        binding.tvVacio.visibility =
            if (listaFiltrada.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun abrirFormulario(producto: Producto?) {
        val intent = Intent(this, ProductoFormActivity::class.java)
        producto?.let { intent.putExtra(ProductoFormActivity.EXTRA_PRODUCTO_ID, it.id) }
        startActivity(intent)
    }

    private fun confirmarEliminar(producto: Producto) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Producto")
            .setMessage("¿Desea eliminar \"${producto.nombre}\"?")
            .setPositiveButton("Sí") { _, _ ->
                val filas = dbHelper.eliminarProducto(producto.id)
                if (filas > 0) {
                    Snackbar.make(binding.root, "Producto eliminado", Snackbar.LENGTH_SHORT).show()
                    cargarProductos()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
