package com.example.proyectointegradordam.managers

import com.example.proyectointegradordam.database.clubDeportivoDBHelper
import android.content.ContentValues
import android.content.Context

import com.example.proyectointegradordam.models.Cliente

class EditClienteManager(private val context: Context) {
    private val dbHelper = clubDeportivoDBHelper(context)



    fun buscarClientePorNombre(texto: String): List<Cliente>{
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM cliente WHERE nombre LIKE ? OR apellido LIKE ?",
            arrayOf("%$texto%", "%$texto%")
        )
        val lista = mutableListOf<Cliente>()
        if(cursor.moveToFirst()){
            do{
                lista.add(
                    Cliente(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista

    }

    fun actualizarDatosCliente(
        id: Int,
        email: String,
        telefono: String
    ): Int{
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("email", email)
            put("telefono", telefono)
        }
        return db.update("cliente", values, "id_cliente = ?", arrayOf(id.toString()))

    }

    fun obtenerTodosLosClientes(): List<Cliente> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM cliente", null)
        val lista = mutableListOf<Cliente>()
        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Cliente(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

}