package com.ferreteria.dbferreteria.clientes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ferreteria.dbferreteria.database.DatabaseHelper
import com.ferreteria.dbferreteria.databinding.ActivityClienteFormBinding

/**
 * ClienteFormActivity — formulario para crear o editar un cliente.
 * Recibe EXTRA_CLIENTE_ID (entero) para edición; si no llega, crea uno nuevo.
 */
class ClienteFormActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CLIENTE_ID = "cliente_id"
    }

    private lateinit var binding: ActivityClienteFormBinding
    private lateinit var dbHelper: DatabaseHelper

    private var clienteId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClienteFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        // Detectar si es creación o edición
        clienteId = intent.getIntExtra(EXTRA_CLIENTE_ID, -1).takeIf { it != -1 }

        supportActionBar?.apply {
            title = if (clienteId == null) "Nuevo Cliente" else "Editar Cliente"
            setDisplayHomeAsUpEnabled(true)
        }

        // Si es edición, cargar datos
        clienteId?.let { cargarCliente(it) }

        binding.btnGuardar.setOnClickListener {
            guardarCliente()
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun cargarCliente(id: Int) {
        val cliente = dbHelper.obtenerClientePorId(id)
        cliente?.let {
            binding.etNombre.setText(it.nombre)
            binding.etDireccion.setText(it.direccion)
            binding.etTelefono.setText(it.telefono)
        }
    }

    private fun guardarCliente() {
        val nombre    = binding.etNombre.text.toString().trim()
        val direccion = binding.etDireccion.text.toString().trim()
        val telefono  = binding.etTelefono.text.toString().trim()

        // Validación básica
        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val resultado = if (clienteId == null) {
            // Crear
            dbHelper.insertarCliente(nombre, direccion, telefono)
        } else {
            // Actualizar
            dbHelper.actualizarCliente(clienteId!!, nombre, direccion, telefono).toLong()
        }

        if (resultado > 0) {
            Toast.makeText(
                this,
                if (clienteId == null) "Cliente creado" else "Cliente actualizado",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
