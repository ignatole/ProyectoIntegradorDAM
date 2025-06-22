package com.example.proyectointegradordam.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradordam.R
import com.example.proyectointegradordam.models.ClienteConVencimiento
import java.text.SimpleDateFormat
import java.util.*

class ExpirationsAdapter(private var clientes: List<ClienteConVencimiento>) :
    RecyclerView.Adapter<ExpirationsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreApellido: TextView = itemView.findViewById(R.id.text_nombre)
        val telefono: TextView = itemView.findViewById(R.id.text_telefono)
        val vencimiento: TextView = itemView.findViewById(R.id.text_vencimiento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expiration, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = clientes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cliente = clientes[position]
        holder.nombreApellido.text = "${cliente.nombre} ${cliente.apellido}"
        holder.telefono.text = cliente.telefono

        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fecha = Date(cliente.vencimiento)
        holder.vencimiento.text = formato.format(fecha)
    }
}
