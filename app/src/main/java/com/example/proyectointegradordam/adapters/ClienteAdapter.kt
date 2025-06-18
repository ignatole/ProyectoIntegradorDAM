package com.example.proyectointegradordam.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradordam.R
import com.example.proyectointegradordam.models.Cliente

class ClienteAdapter (
    private var clientes: List<Cliente> = listOf(),
    private val onClick: (Cliente) -> Unit
) : RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>(){

    inner class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvNombreCompleto = itemView.findViewById<TextView>(R.id.tvNombreCompleto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cliente, parent, false)
        return ClienteViewHolder(view)
    }
    override fun onBindViewHolder(holder:ClienteViewHolder, position: Int){
        val cliente = clientes[position]
        holder.tvNombreCompleto.text = "${cliente.nombre} ${cliente.apellido}"
        holder.itemView.setOnClickListener{
            onClick(cliente)
        }
    }

    override fun getItemCount() = clientes.size

    fun updateData(nuevaLista: List<Cliente>){
        clientes = nuevaLista
        notifyDataSetChanged()
    }
}