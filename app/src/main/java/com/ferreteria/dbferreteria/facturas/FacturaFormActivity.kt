package com.ferreteria.dbferreteria.facturas

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ferreteria.dbferreteria.database.DatabaseHelper
import com.ferreteria.dbferreteria.databinding.ActivityFacturaFormBinding
import com.ferreteria.dbferreteria.model.Pedido
import java.util.Calendar

/**
 * FacturaFormActivity — formulario para crear o editar una factura.
 * Carga los pedidos en un Spinner y abre DatePickerDialog para la fecha.
 */
class FacturaFormActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FACTURA_ID = "factura_id"
    }

    private lateinit var binding: ActivityFacturaFormBinding
    private lateinit var dbHelper: DatabaseHelper
    private var facturaId: Int? = null

    private var pedidos: List<Pedido> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacturaFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        facturaId = intent.getIntExtra(EXTRA_FACTURA_ID, -1).takeIf { it != -1 }

        supportActionBar?.apply {
            title = if (facturaId == null) "Nueva Factura" else "Editar Factura"
            setDisplayHomeAsUpEnabled(true)
        }

        cargarSpinnerPedidos()

        // DatePicker al tocar el campo fecha
        binding.etFecha.setOnClickListener { mostrarDatePicker() }

        facturaId?.let { cargarFactura(it) }

        binding.btnGuardar.setOnClickListener  { guardarFactura() }
        binding.btnCancelar.setOnClickListener { finish() }
    }

    private fun cargarSpinnerPedidos() {
        pedidos = dbHelper.obtenerPedidos()
        if (pedidos.isEmpty()) {
            Toast.makeText(
                this,
                "Primero registre al menos un pedido",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        val etiquetas = pedidos.map { "Pedido #${it.id} — ${it.nombreCliente}" }
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            etiquetas
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPedido.adapter = spinnerAdapter
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

    private fun cargarFactura(id: Int) {
        dbHelper.obtenerFacturaPorId(id)?.let { factura ->
            binding.etFecha.setText(factura.fecha)
            binding.etValorTotal.setText(factura.valorTotal.toString())
            val indice = pedidos.indexOfFirst { it.id == factura.codigoPedido }
            if (indice >= 0) binding.spinnerPedido.setSelection(indice)
        }
    }

    private fun guardarFactura() {
        val fecha      = binding.etFecha.text.toString().trim()
        val totalStr   = binding.etValorTotal.text.toString().trim()

        if (fecha.isEmpty() || totalStr.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val valorTotal = totalStr.toDoubleOrNull()
        if (valorTotal == null || valorTotal < 0) {
            Toast.makeText(this, "Ingrese un valor numérico válido", Toast.LENGTH_SHORT).show()
            return
        }

        val pedidoSeleccionado = pedidos[binding.spinnerPedido.selectedItemPosition]

        val ok = if (facturaId == null) {
            dbHelper.insertarFactura(pedidoSeleccionado.id, fecha, valorTotal) > 0
        } else {
            dbHelper.actualizarFactura(facturaId!!, pedidoSeleccionado.id, fecha, valorTotal) > 0
        }

        if (ok) {
            Toast.makeText(
                this,
                if (facturaId == null) "Factura creada" else "Factura actualizada",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
