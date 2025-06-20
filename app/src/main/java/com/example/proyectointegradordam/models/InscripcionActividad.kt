package com.example.proyectointegradordam.models

data class InscripcionActividad(
    val idInscripcion: Int,
    val idCliente: Int,
    val idActividad: Int,
    val nombreCliente: String,
    val nombreActividad: String,
    val dia: String,
    val fechaInscripcion: String
)
