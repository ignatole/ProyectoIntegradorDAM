package com.example.proyectointegradordam.adapters

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
        holder.binding.tvActivity.text = activities.name
        holder.binding.tvDateTime.text = activities.date
        holder.binding.tvCupo.text = "2 / ${activities.cupo}"
    }
}