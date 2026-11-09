package com.ferreteria.dbferreteria

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ferreteria.dbferreteria.clientes.ClientesActivity
import com.ferreteria.dbferreteria.databinding.ActivityMainBinding
import com.ferreteria.dbferreteria.facturas.FacturasActivity
import com.ferreteria.dbferreteria.pedidos.PedidosActivity
import com.ferreteria.dbferreteria.productos.ProductosActivity

/**
 * MainActivity — Menú principal de la aplicación.
 * Muestra las 4 tarjetas de acceso a cada módulo CRUD.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Ferretería Rodamientos y Fierros"

        // Navegar a Clientes
        binding.cardClientes.setOnClickListener {
            startActivity(Intent(this, ClientesActivity::class.java))
        }

        // Navegar a Productos
        binding.cardProductos.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }

        // Navegar a Pedidos
        binding.cardPedidos.setOnClickListener {
            startActivity(Intent(this, PedidosActivity::class.java))
        }

        // Navegar a Facturas
        binding.cardFacturas.setOnClickListener {
            startActivity(Intent(this, FacturasActivity::class.java))
        }
    }
}
