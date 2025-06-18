package com.example.proyectointegradordam.managers

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.proyectointegradordam.database.clubDeportivoDBHelper


class ActivitiesManager(private val context: Context) {

    data class Actividad(
        val id: Int? = null, // El id es opcional
        val nombre: String,
        val horario: String,
        val dia: String,
        val profesor: String,
        val costo: Float,
        val cupo: Int,
    )

    private val dbHelper = clubDeportivoDBHelper(context)

    fun insertarActividad(actividad: Actividad): Boolean {
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

            if (result != -1L) {
                Log.d("ActivitiesManager", "Actividad insertada correctamente con ID: $result")
                true
            } else {
                Log.e("ActivitiesManager", "Error al insertar actividad")
                false
            }
        } catch (e: Exception) {
            Log.e("ActivitiesManager", "Error en insertarActividad: ${e.message}")
            false
        }
    }

    fun obtenerActividades(): List<Actividad> {
        val lista = mutableListOf<Actividad>()
        return try {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM actividad ORDER BY id_actividad DESC", null)

            if (cursor.moveToFirst()) {
                do {
                    val actividad = Actividad(
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
            Log.d("ActivitiesManager", "Se obtuvieron ${lista.size} actividades")
            lista
        } catch (e: Exception) {
            Log.e("ActivitiesManager", "Error en obtenerActividades: ${e.message}")
            lista
        }
    }

    fun actualizarActividad(actividad: Actividad): Boolean {
        return try {
            if (actividad.id == null) {
                Log.e("ActivitiesManager", "No se puede actualizar actividad sin ID")
                return false
            }

            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("nombre", actividad.nombre)
                put("horario", actividad.horario)
                put("dia", actividad.dia)
                put("profesor", actividad.profesor)
                put("costo", actividad.costo)
                put("cupo", actividad.cupo)
            }
            val result =
                db.update("actividad", values, "id_actividad=?", arrayOf(actividad.id.toString()))
            db.close()

            if (result > 0) {
                Log.d("ActivitiesManager", "Actividad actualizada correctamente")
                true
            } else {
                Log.e("ActivitiesManager", "No se pudo actualizar la actividad")
                false
            }
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

            if (result > 0) {
                Log.d("ActivitiesManager", "Actividad eliminada correctamente")
                true
            } else {
                Log.e("ActivitiesManager", "No se pudo eliminar la actividad")
                false
            }
        } catch (e: Exception) {
            Log.e("ActivitiesManager", "Error en eliminarActividad: ${e.message}")
            false
        }
    }
}

