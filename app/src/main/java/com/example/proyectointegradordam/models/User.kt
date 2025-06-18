package com.example.proyectointegradordam.models

data class User(
    val id: Int,
    val username: String,
    val name: String,
    val cellphone: String,
    val password: String,
    val isActive: Boolean
)
