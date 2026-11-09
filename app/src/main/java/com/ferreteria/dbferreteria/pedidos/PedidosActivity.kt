package com.ferreteria.dbferreteria.pedidos

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferreteria.dbferreteria.adapter.GenericoAdapter
import com.ferreteria.dbferreteria.database.DatabaseHelper
import com.ferreteria.dbferreteria.databinding.ActivityPedidosBinding
import com.ferreteria.dbferreteria.model.Pedido
import com.google.android.material.snackbar.Snackbar

/**
 * PedidosActivity — lista todos los pedidos con búsqueda y CRUD completo.
 */
class PedidosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidosBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: GenericoAdapter<Pedido>

    private var listaMaestra: List<Pedido> = emptyList()
    private val listaFiltrada: MutableList<Pedido> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Pedidos"
            setDisplayHomeAsUpEnabled(true)
        }

        dbHelper = DatabaseHelper(this)

        adapter = GenericoAdapter(
            items      = listaFiltrada,
            titulo     = { "Pedido #${it.id} — ${it.nombreCliente}" },
            subtitulo  = { "📝 ${it.descripcion}" },
            detalle    = { "📅 ${it.fecha}" },
            onEditar   = { pos -> abrirFormulario(listaFiltrada[pos]) },
            onEliminar = { pos -> confirmarEliminar(listaFiltrada[pos]) }
        )

        binding.recyclerPedidos.layoutManager = LinearLayoutManager(this)
        binding.recyclerPedidos.adapter = adapter

        binding.fabNuevoPedido.setOnClickListener {
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
        cargarPedidos()
    }

    private fun cargarPedidos() {
        listaMaestra = dbHelper.obtenerPedidos()
        filtrar(binding.etBuscar.text.toString())
    }

    private fun filtrar(query: String) {
        val resultado = if (query.isBlank()) {
            listaMaestra
        } else {
            listaMaestra.filter {
                it.nombreCliente.contains(query, ignoreCase = true) ||
                it.descripcion.contains(query, ignoreCase = true) ||
                it.fecha.contains(query, ignoreCase = true)
            }
        }

        listaFiltrada.clear()
        listaFiltrada.addAll(resultado)
        adapter.notifyDataSetChanged()

        binding.tvContador.text = "${listaFiltrada.size} registro(s)"
        binding.recyclerPedidos.visibility =
            if (listaFiltrada.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE
        binding.tvVacio.visibility =
            if (listaFiltrada.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun abrirFormulario(pedido: Pedido?) {
        val intent = Intent(this, PedidoFormActivity::class.java)
        pedido?.let { intent.putExtra(PedidoFormActivity.EXTRA_PEDIDO_ID, it.id) }
        startActivity(intent)
    }

    private fun confirmarEliminar(pedido: Pedido) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Pedido")
            .setMessage("¿Desea eliminar el pedido #${pedido.id}?")
            .setPositiveButton("Sí") { _, _ ->
                val filas = dbHelper.eliminarPedido(pedido.id)
                if (filas > 0) {
                    Snackbar.make(binding.root, "Pedido eliminado", Snackbar.LENGTH_SHORT).show()
                    cargarPedidos()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
