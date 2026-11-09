package com.ferreteria.dbferreteria.clientes

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferreteria.dbferreteria.adapter.GenericoAdapter
import com.ferreteria.dbferreteria.database.DatabaseHelper
import com.ferreteria.dbferreteria.databinding.ActivityClientesBinding
import com.ferreteria.dbferreteria.model.Cliente
import com.google.android.material.snackbar.Snackbar

/**
 * ClientesActivity — lista todos los clientes con búsqueda en tiempo real.
 * Permite crear (FAB), editar y eliminar desde cada ítem del RecyclerView.
 */
class ClientesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientesBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: GenericoAdapter<Cliente>

    // Lista maestra (sin filtrar) y lista filtrada
    private var listaMaestra: List<Cliente> = emptyList()
    private val listaFiltrada: MutableList<Cliente> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Clientes"
            setDisplayHomeAsUpEnabled(true)
        }

        dbHelper = DatabaseHelper(this)

        // Configurar RecyclerView con el adaptador genérico
        adapter = GenericoAdapter(
            items      = listaFiltrada,
            titulo     = { it.nombre },
            subtitulo  = { "📍 ${it.direccion}" },
            detalle    = { "📞 ${it.telefono}" },
            onEditar   = { pos -> abrirFormulario(listaFiltrada[pos]) },
            onEliminar = { pos -> confirmarEliminar(listaFiltrada[pos]) }
        )

        binding.recyclerClientes.layoutManager = LinearLayoutManager(this)
        binding.recyclerClientes.adapter = adapter

        // FAB → nuevo cliente
        binding.fabNuevoCliente.setOnClickListener {
            abrirFormulario(null)
        }

        // Búsqueda en tiempo real
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
        cargarClientes()
    }

    private fun cargarClientes() {
        listaMaestra = dbHelper.obtenerClientes()
        filtrar(binding.etBuscar.text.toString())
    }

    private fun filtrar(query: String) {
        val resultado = if (query.isBlank()) {
            listaMaestra
        } else {
            listaMaestra.filter {
                it.nombre.contains(query, ignoreCase = true) ||
                it.direccion.contains(query, ignoreCase = true) ||
                it.telefono.contains(query, ignoreCase = true)
            }
        }

        listaFiltrada.clear()
        listaFiltrada.addAll(resultado)
        adapter.notifyDataSetChanged()

        // Mostrar estado vacío si no hay resultados
        binding.tvContador.text = "${listaFiltrada.size} registro(s)"
        if (listaFiltrada.isEmpty()) {
            binding.recyclerClientes.visibility = android.view.View.GONE
            binding.tvVacio.visibility = android.view.View.VISIBLE
        } else {
            binding.recyclerClientes.visibility = android.view.View.VISIBLE
            binding.tvVacio.visibility = android.view.View.GONE
        }
    }

    private fun abrirFormulario(cliente: Cliente?) {
        val intent = Intent(this, ClienteFormActivity::class.java)
        cliente?.let { intent.putExtra(ClienteFormActivity.EXTRA_CLIENTE_ID, it.id) }
        startActivity(intent)
    }

    private fun confirmarEliminar(cliente: Cliente) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Cliente")
            .setMessage("¿Desea eliminar a \"${cliente.nombre}\"?")
            .setPositiveButton("Sí") { _, _ ->
                val filas = dbHelper.eliminarCliente(cliente.id)
                if (filas > 0) {
                    Snackbar.make(binding.root, "Cliente eliminado", Snackbar.LENGTH_SHORT).show()
                    cargarClientes()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
