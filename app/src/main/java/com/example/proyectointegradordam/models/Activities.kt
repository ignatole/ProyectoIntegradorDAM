package com.example.proyectointegradordam.models

data class Activities(
    val id: Int? = null,
    val nombre: String,
    val horario: String,
    val dia: String,
    val profesor: String,
    val costo: Float,
    val cupo: Int
)
