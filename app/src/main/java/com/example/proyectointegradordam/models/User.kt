package com.example.proyectointegradordam.models

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val isActive: Boolean
)
