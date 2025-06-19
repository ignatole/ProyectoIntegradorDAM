package com.example.proyectointegradordam.managers

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.proyectointegradordam.database.clubDeportivoDBHelper
import com.example.proyectointegradordam.models.Activities

class ActivitiesManager(private val context: Context) {

    private val dbHelper = clubDeportivoDBHelper(context)

    fun insertarActividad(actividad: Activities): Boolean {
        return try {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("nombre", actividad.nombre)
                put("horario", actividad.horario)
                put("dia", actividad.dia)
                put("profesor", actividad.profesor)
                put("costo", actividad.costo)
                put("cupo", actividad.cupo)
            }
            val result = db.insert("actividad", null, values)
            db.close()

            result != -1L
        } catch (e: Exception) {
            Log.e("ActivitiesManager", "Error en insertarActividad: ${e.message}")
            false
        }
    }

    fun obtenerActividades(): List<Activities> {
        val lista = mutableListOf<Activities>()
        return try {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM actividad ORDER BY id_actividad DESC", null)

            if (cursor.moveToFirst()) {
                do {
                    val actividad = Activities(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id_actividad")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        horario = cursor.getString(cursor.getColumnIndexOrThrow("horario")),
                        dia = cursor.getString(cursor.getColumnIndexOrThrow("dia")),
                        profesor = cursor.getString(cursor.getColumnIndexOrThrow("profesor")),
                        costo = cursor.getFloat(cursor.getColumnIndexOrThrow("costo")),
                        cupo = cursor.getInt(cursor.getColumnIndexOrThrow("cupo"))
                    )
                    lista.add(actividad)
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()
            lista
        } catch (e: Exception) {
            Log.e("ActivitiesManager", "Error en obtenerActividades: ${e.message}")
            lista
        }
    }

    fun actualizarActividad(actividad: Activities): Boolean {
        return try {
            if (actividad.id == null) return false

            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("nombre", actividad.nombre)
                put("horario", actividad.horario)
                put("dia", actividad.dia)
                put("profesor", actividad.profesor)
                put("costo", actividad.costo)
                put("cupo", actividad.cupo)
            }
            val result = db.update("actividad", values, "id_actividad=?", arrayOf(actividad.id.toString()))
            db.close()

            result > 0
        } catch (e: Exception) {
            Log.e("ActivitiesManager", "Error en actualizarActividad: ${e.message}")
            false
        }
    }

    fun eliminarActividad(id: Int): Boolean {
        return try {
            val db = dbHelper.writableDatabase
            val result = db.delete("actividad", "id_actividad=?", arrayOf(id.toString()))
            db.close()

            result > 0
        } catch (e: Exception) {
            Log.e("ActivitiesManager", "Error en eliminarActividad: ${e.message}")
            false
        }
    }
}