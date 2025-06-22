package com.example.proyectointegradordam.managers

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.proyectointegradordam.database.clubDeportivoDBHelper
import com.example.proyectointegradordam.models.Activities
import com.example.proyectointegradordam.models.InscripcionActividad

class InscripcionManager(private val context: Context) {

    private val dbHelper = clubDeportivoDBHelper(context)

    fun inscribirClienteEnActividad(idCliente: Int, idActividad: Int): Pair<Boolean, String> {
        val db = dbHelper.writableDatabase
        return try {
            db.beginTransaction()

            // Obtener timestamp actual en milisegundos
            val ahora = System.currentTimeMillis()

            // Validar cuotas activas y no vencidas (tipo 1 o 2)
            val cursorCuotas = db.rawQuery("""
            SELECT COUNT(*) FROM cuota
            WHERE id_cliente = ?
              AND tipo_cuota IN (1, 2)
              AND fecha_vencimiento >= ?
        """, arrayOf(idCliente.toString(), ahora.toString()))

            cursorCuotas.moveToFirst()
            val cuotasVigentes = cursorCuotas.getInt(0) > 0
            cursorCuotas.close()

            if (!cuotasVigentes) {
                db.endTransaction()
                return Pair(false, "El cliente no tiene creditos vigentes para este mes")
            }

            // Verificar si la actividad tiene cupos disponibles
            val cursorActividad = db.rawQuery(
                "SELECT cupo FROM actividad WHERE id_actividad = ?",
                arrayOf(idActividad.toString())
            )

            if (!cursorActividad.moveToFirst()) {
                cursorActividad.close()
                db.endTransaction()
                return Pair(false, "Actividad no encontrada")
            }

            val cupoActual = cursorActividad.getInt(0)
            cursorActividad.close()

            if (cupoActual <= 0) {
                db.endTransaction()
                return Pair(false, "No hay cupos disponibles")
            }

            // Verificar si el cliente ya está inscrito
            val cursorInscripcion = db.rawQuery(
                "SELECT COUNT(*) FROM inscripcion_actividad WHERE id_cliente = ? AND id_actividad = ? AND activo = 1",
                arrayOf(idCliente.toString(), idActividad.toString())
            )

            cursorInscripcion.moveToFirst()
            val yaInscrito = cursorInscripcion.getInt(0) > 0
            cursorInscripcion.close()

            if (yaInscrito) {
                db.endTransaction()
                return Pair(false, "El cliente ya está inscrito en esta actividad")
            }

            // Insertar la inscripción
            val valuesInscripcion = ContentValues().apply {
                put("id_cliente", idCliente)
                put("id_actividad", idActividad)
                put("fecha_inscripcion", getCurrentDateTime())
                put("activo", 1)
            }

            val resultInscripcion = db.insert("inscripcion_actividad", null, valuesInscripcion)

            if (resultInscripcion == -1L) {
                db.endTransaction()
                return Pair(false, "Error al crear la inscripción")
            }

            // Decrementar el cupo de la actividad
            val valuesActividad = ContentValues().apply {
                put("cupo", cupoActual - 1)
            }

            val resultActividad = db.update(
                "actividad",
                valuesActividad,
                "id_actividad = ?",
                arrayOf(idActividad.toString())
            )

            if (resultActividad <= 0) {
                db.endTransaction()
                return Pair(false, "Error al actualizar el cupo")
            }

            db.setTransactionSuccessful()
            db.endTransaction()

            Pair(true, "Cliente inscrito exitosamente")

        } catch (e: Exception) {
            db.endTransaction()
            Log.e("InscripcionManager", "Error en inscribirClienteEnActividad: ${e.message}")
            Pair(false, "Error interno: ${e.message}")
        } finally {
            db.close()
        }
    }



    fun cancelarInscripcion(idInscripcion: Int): Pair<Boolean, String> {
        val db = dbHelper.writableDatabase
        return try {
            db.beginTransaction()

            // Obtener la información de la inscripción antes de cancelarla
            val cursorInscripcion = db.rawQuery(
                "SELECT id_actividad FROM inscripcion_actividad WHERE id_inscripcion = ? AND activo = 1",
                arrayOf(idInscripcion.toString())
            )

            if (!cursorInscripcion.moveToFirst()) {
                cursorInscripcion.close()
                db.endTransaction()
                return Pair(false, "Inscripción no encontrada o ya cancelada")
            }

            val idActividad = cursorInscripcion.getInt(0)
            cursorInscripcion.close()

            // Marcar la inscripción como inactiva
            val valuesInscripcion = ContentValues().apply {
                put("activo", 0)
            }

            val resultInscripcion = db.update(
                "inscripcion_actividad",
                valuesInscripcion,
                "id_inscripcion = ?",
                arrayOf(idInscripcion.toString())
            )

            if (resultInscripcion <= 0) {
                db.endTransaction()
                return Pair(false, "Error al cancelar la inscripción")
            }

            // Incrementar el cupo de la actividad
            val cursorActividad = db.rawQuery(
                "SELECT cupo FROM actividad WHERE id_actividad = ?",
                arrayOf(idActividad.toString())
            )

            if (cursorActividad.moveToFirst()) {
                val cupoActual = cursorActividad.getInt(0)
                cursorActividad.close()

                val valuesActividad = ContentValues().apply {
                    put("cupo", cupoActual + 1)
                }

                val resultActividad = db.update(
                    "actividad",
                    valuesActividad,
                    "id_actividad = ?",
                    arrayOf(idActividad.toString())
                )

                if (resultActividad <= 0) {
                    db.endTransaction()
                    return Pair(false, "Error al actualizar el cupo de la actividad")
                }
            } else {
                cursorActividad.close()
                db.endTransaction()
                return Pair(false, "Actividad no encontrada")
            }

            db.setTransactionSuccessful()
            db.endTransaction()

            Pair(true, "Inscripción cancelada exitosamente")

        } catch (e: Exception) {
            db.endTransaction()
            Log.e("InscripcionManager", "Error en cancelarInscripcion: ${e.message}")
            Pair(false, "Error interno: ${e.message}")
        } finally {
            db.close()
        }
    }

    fun obtenerActividadesConCupo(): List<Activities> {
        val lista = mutableListOf<Activities>()
        return try {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM actividad WHERE cupo > 0 ORDER BY nombre",
                null
            )

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
            Log.e("InscripcionManager", "Error en obtenerActividadesConCupo: ${e.message}")
            lista
        }
    }

    fun obtenerInscripcionesCliente(idCliente: Int): List<InscripcionActividad> {
        val lista = mutableListOf<InscripcionActividad>()
        return try {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(
                """
                SELECT 
                    i.id_inscripcion,
                    i.id_cliente,
                    i.id_actividad,
                    c.nombre || ' ' || c.apellido as nombre_cliente,
                    a.nombre as nombre_actividad,
                    a.dia,
                    i.fecha_inscripcion
                FROM inscripcion_actividad i
                INNER JOIN cliente c ON i.id_cliente = c.id_cliente
                INNER JOIN actividad a ON i.id_actividad = a.id_actividad
                WHERE i.id_cliente = ? AND i.activo = 1
                ORDER BY i.fecha_inscripcion DESC
                """,
                arrayOf(idCliente.toString())
            )

            if (cursor.moveToFirst()) {
                do {
                    val inscripcion = InscripcionActividad(
                        idInscripcion = cursor.getInt(cursor.getColumnIndexOrThrow("id_inscripcion")),
                        idCliente = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente")),
                        idActividad = cursor.getInt(cursor.getColumnIndexOrThrow("id_actividad")),
                        nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow("nombre_cliente")),
                        nombreActividad = cursor.getString(cursor.getColumnIndexOrThrow("nombre_actividad")),
                        dia = cursor.getString(cursor.getColumnIndexOrThrow("dia")),
                        fechaInscripcion = cursor.getString(cursor.getColumnIndexOrThrow("fecha_inscripcion"))
                    )
                    lista.add(inscripcion)
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()
            lista
        } catch (e: Exception) {
            Log.e("InscripcionManager", "Error en obtenerInscripcionesCliente: ${e.message}")
            lista
        }
    }

    private fun getCurrentDateTime(): String {
        val formato = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault())
        return formato.format(java.util.Date())
    }
}