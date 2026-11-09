package com.ferreteria.dbferreteria.pedidos

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ferreteria.dbferreteria.database.DatabaseHelper
import com.ferreteria.dbferreteria.databinding.ActivityPedidoFormBinding
import com.ferreteria.dbferreteria.model.Cliente
import java.util.Calendar

/**
 * PedidoFormActivity — formulario para crear o editar un pedido.
 * Carga los clientes en un Spinner y abre un DatePickerDialog para la fecha.
 */
class PedidoFormActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PEDIDO_ID = "pedido_id"
    }

    private lateinit var binding: ActivityPedidoFormBinding
    private lateinit var dbHelper: DatabaseHelper
    private var pedidoId: Int? = null

    // Lista de clientes para el Spinner
    private var clientes: List<Cliente> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        pedidoId = intent.getIntExtra(EXTRA_PEDIDO_ID, -1).takeIf { it != -1 }

        supportActionBar?.apply {
            title = if (pedidoId == null) "Nuevo Pedido" else "Editar Pedido"
            setDisplayHomeAsUpEnabled(true)
        }

        cargarSpinnerClientes()

        // DatePicker al tocar el campo fecha
        binding.etFecha.setOnClickListener { mostrarDatePicker() }

        pedidoId?.let { cargarPedido(it) }

        binding.btnGuardar.setOnClickListener  { guardarPedido() }
        binding.btnCancelar.setOnClickListener { finish() }
    }

    private fun cargarSpinnerClientes() {
        clientes = dbHelper.obtenerClientes()
        if (clientes.isEmpty()) {
            Toast.makeText(this, "Primero registre al menos un cliente", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        val nombresClientes = clientes.map { it.nombre }
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            nombresClientes
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCliente.adapter = spinnerAdapter
    }

    private fun mostrarDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                binding.etFecha.setText("%04d-%02d-%02d".format(year, month + 1, day))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun cargarPedido(id: Int) {
        dbHelper.obtenerPedidoPorId(id)?.let { pedido ->
            binding.etDescripcion.setText(pedido.descripcion)
            binding.etFecha.setText(pedido.fecha)
            // Seleccionar el cliente correcto en el Spinner
            val indice = clientes.indexOfFirst { it.id == pedido.codigoCliente }
            if (indice >= 0) binding.spinnerCliente.setSelection(indice)
        }
    }

    private fun guardarPedido() {
        val descripcion = binding.etDescripcion.text.toString().trim()
        val fecha       = binding.etFecha.text.toString().trim()

        if (descripcion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val clienteSeleccionado = clientes[binding.spinnerCliente.selectedItemPosition]

        val ok = if (pedidoId == null) {
            dbHelper.insertarPedido(clienteSeleccionado.id, descripcion, fecha) > 0
        } else {
            dbHelper.actualizarPedido(pedidoId!!, clienteSeleccionado.id, descripcion, fecha) > 0
        }

        if (ok) {
            Toast.makeText(
                this,
                if (pedidoId == null) "Pedido creado" else "Pedido actualizado",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
