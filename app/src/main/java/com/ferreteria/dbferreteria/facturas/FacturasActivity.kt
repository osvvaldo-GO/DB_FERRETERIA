package com.ferreteria.dbferreteria.facturas

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferreteria.dbferreteria.adapter.GenericoAdapter
import com.ferreteria.dbferreteria.database.DatabaseHelper
import com.ferreteria.dbferreteria.databinding.ActivityFacturasBinding
import com.ferreteria.dbferreteria.model.Factura
import com.google.android.material.snackbar.Snackbar

/**
 * FacturasActivity — lista todas las facturas con búsqueda y CRUD completo.
 */
class FacturasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFacturasBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: GenericoAdapter<Factura>

    private var listaMaestra: List<Factura> = emptyList()
    private val listaFiltrada: MutableList<Factura> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacturasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Facturas"
            setDisplayHomeAsUpEnabled(true)
        }

        dbHelper = DatabaseHelper(this)

        adapter = GenericoAdapter(
            items      = listaFiltrada,
            titulo     = { "Factura #${it.id}" },
            subtitulo  = { "📋 Pedido: ${it.descPedido}" },
            detalle    = { "💰 Total: $${"%.2f".format(it.valorTotal)}  📅 ${it.fecha}" },
            onEditar   = { pos -> abrirFormulario(listaFiltrada[pos]) },
            onEliminar = { pos -> confirmarEliminar(listaFiltrada[pos]) }
        )

        binding.recyclerFacturas.layoutManager = LinearLayoutManager(this)
        binding.recyclerFacturas.adapter = adapter

        binding.fabNuevaFactura.setOnClickListener {
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
        cargarFacturas()
    }

    private fun cargarFacturas() {
        listaMaestra = dbHelper.obtenerFacturas()
        filtrar(binding.etBuscar.text.toString())
    }

    private fun filtrar(query: String) {
        val resultado = if (query.isBlank()) {
            listaMaestra
        } else {
            listaMaestra.filter {
                it.descPedido.contains(query, ignoreCase = true) ||
                it.fecha.contains(query, ignoreCase = true) ||
                it.valorTotal.toString().contains(query)
            }
        }

        listaFiltrada.clear()
        listaFiltrada.addAll(resultado)
        adapter.notifyDataSetChanged()

        binding.tvContador.text = "${listaFiltrada.size} registro(s)"
        binding.recyclerFacturas.visibility =
            if (listaFiltrada.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE
        binding.tvVacio.visibility =
            if (listaFiltrada.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun abrirFormulario(factura: Factura?) {
        val intent = Intent(this, FacturaFormActivity::class.java)
        factura?.let { intent.putExtra(FacturaFormActivity.EXTRA_FACTURA_ID, it.id) }
        startActivity(intent)
    }

    private fun confirmarEliminar(factura: Factura) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Factura")
            .setMessage("¿Desea eliminar la Factura #${factura.id}?")
            .setPositiveButton("Sí") { _, _ ->
                val filas = dbHelper.eliminarFactura(factura.id)
                if (filas > 0) {
                    Snackbar.make(binding.root, "Factura eliminada", Snackbar.LENGTH_SHORT).show()
                    cargarFacturas()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
