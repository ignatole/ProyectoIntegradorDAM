package com.example.proyectointegradordam.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradordam.ExpiredMember
import com.example.proyectointegradordam.R

class ExpirationAdapter (private val lista: List<ExpiredMember>
) : RecyclerView.Adapter<ExpirationAdapter.ExpirationViewHolder>(){

    class ExpirationViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val txtNombre : TextView = itemView.findViewById(R.id.txtNombreCompleto)
        val txtDni : TextView = itemView.findViewById(R.id.txtDni)
        val txtFecha : TextView = itemView.findViewById(R.id.txtFecha)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpirationViewHolder{
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expiration, parent, false)
        return ExpirationViewHolder(view)
    }
    override fun onBindViewHolder(holder: ExpirationViewHolder, position: Int){
        val member = lista[position]
        holder.txtNombre.text = member.nombreCompleto
        holder.txtDni.text = member.dni
        holder.txtFecha.text = member.fecha
    }
    override  fun getItemCount(): Int = lista.size
}