package com.ferreteria.dbferreteria.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ferreteria.dbferreteria.databinding.ItemListaGenericoBinding

/**
 * GenericoAdapter — adaptador reutilizable para los RecyclerView de todas las entidades.
 *
 * @param T          tipo de dato (Cliente, Producto, Pedido, Factura)
 * @param items      lista mutable de elementos a mostrar
 * @param titulo     función que extrae el texto del título para cada ítem
 * @param subtitulo  función que extrae el texto del subtítulo para cada ítem
 * @param detalle    función opcional que extrae un texto adicional (precio, fecha…)
 * @param onEditar   callback invocado con el índice cuando se pulsa ✏
 * @param onEliminar callback invocado con el índice cuando se pulsa 🗑
 */
class GenericoAdapter<T>(
    private val items: MutableList<T>,
    private val titulo: (T) -> String,
    private val subtitulo: (T) -> String,
    private val detalle: ((T) -> String)? = null,
    private val onEditar: (Int) -> Unit,
    private val onEliminar: (Int) -> Unit
) : RecyclerView.Adapter<GenericoAdapter<T>.ViewHolder>() {

    inner class ViewHolder(private val b: ItemListaGenericoBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: T) {
            b.tvTitulo.text    = titulo(item)
            b.tvSubtitulo.text = subtitulo(item)

            if (detalle != null) {
                b.tvDetalle.text       = detalle.invoke(item)
                b.tvDetalle.visibility = android.view.View.VISIBLE
            } else {
                b.tvDetalle.visibility = android.view.View.GONE
            }

            // bindingAdapterPosition evita posiciones obsoletas tras notifyDataSetChanged
            b.btnEditar.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_ID.toInt()) onEditar(pos)
            }
            b.btnEliminar.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_ID.toInt()) onEliminar(pos)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListaGenericoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    /** Reemplaza la lista completa y notifica al RecyclerView. */
    fun actualizarLista(nuevaLista: List<T>) {
        items.clear()
        items.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}
