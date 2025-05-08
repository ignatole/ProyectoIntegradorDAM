package com.example.proyectointegradordam.adapters

import android.text.Spannable
import android.text.SpannableString
import android.text.SpannedString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradordam.databinding.ItemActivityBinding
import com.example.proyectointegradordam.view.Activities

class ActivityAdapter(private val lista: List<Activities>):
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>(){

    inner class ActivityViewHolder(val binding: ItemActivityBinding):
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding = ItemActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActivityViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activities = lista[position]

        //SUBRAYAR TEXTO
        val texto = activities.name
        val subrayado = SpannableString(texto)
        subrayado.setSpan(
            UnderlineSpan(),
            0,
            texto.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        holder.binding.tvActivity.text = subrayado
        holder.binding.tvDateTime.text = activities.date
        holder.binding.tvCupo.text = "2 / ${activities.cupo}"
    }
}