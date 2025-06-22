package com.example.proyectointegradordam.managers

import android.content.ContentValues
import android.content.Context
import com.example.proyectointegradordam.database.clubDeportivoDBHelper
import com.example.proyectointegradordam.models.Cliente
import com.example.proyectointegradordam.models.ClienteConVencimiento

class ClienteManager(context: Context) {

    private val dbHelper = clubDeportivoDBHelper(context)

    fun buscarClientePorNombre(texto: String): List<Cliente> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM cliente WHERE nombre LIKE ? OR apellido LIKE ?",
            arrayOf("%$texto%", "%$texto%")
        )
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

    fun actualizarDatosCliente(id: Int, email: String, telefono: String): Int {
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

    fun obtenerClientesConVencimientos(): List<ClienteConVencimiento> {
        val lista = mutableListOf<ClienteConVencimiento>()
        val db = dbHelper.readableDatabase

        val query = """
            SELECT c.nombre, c.apellido, c.telefono, MAX(q.fecha_vencimiento) as vencimiento
            FROM cliente c
            LEFT JOIN cuota q ON c.id_cliente = q.id_cliente
            GROUP BY c.id_cliente
        """.trimIndent()

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
                val telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
                val vencimiento = cursor.getLong(cursor.getColumnIndexOrThrow("vencimiento")) // es String, no Long

                lista.add(ClienteConVencimiento(nombre, apellido, telefono, vencimiento))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }
}
