package com.example.proyectointegradordam.managers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.example.proyectointegradordam.database.clubDeportivoDBHelper
import com.example.proyectointegradordam.models.Cliente

class PaymentManager(context: Context) {

    private val dbHelper = clubDeportivoDBHelper(context)

    fun registrarPagoCuota(
        cliente: Cliente,
        monto: Double,
        medioPago: String,
        tipo: String,
        plazo: Int
    ): Pair<Boolean, Long> {
        return try {
            val db = dbHelper.writableDatabase
            val fechaPago = System.currentTimeMillis()

            var ultimaFechaVencimiento = obtenerUltimaFechaVencimiento(cliente.id)
            if (ultimaFechaVencimiento < fechaPago) {
                ultimaFechaVencimiento = fechaPago
            }

            var nuevaFechaVencimiento: Long = ultimaFechaVencimiento

            for (i in 1..plazo) {
                nuevaFechaVencimiento += 30L * 24 * 60 * 60 * 1000
                val values = ContentValues().apply {
                    put("fecha_pago", fechaPago)
                    put("fecha_vencimiento", nuevaFechaVencimiento)
                    put("medio_pago", medioPago)
                    put("monto", monto)
                    put("tipo_cuota", if (tipo == "mensual") 1 else 2)
                    put("plazo_cuota", plazo)
                    put("id_cliente", cliente.id)
                }
                db.insert("cuota", null, values)
            }

            db.close()
            return Pair(true, nuevaFechaVencimiento)
        } catch (e: Exception) {
            Log.e("PaymentManager", "Error al registrar pago: ${e.message}")
            return Pair(false, 0L)
        }
    }

    private fun obtenerUltimaFechaVencimiento(clienteId: Int): Long {
        val dbLectura = dbHelper.readableDatabase
        val cursor: Cursor = dbLectura.rawQuery(
            "SELECT MAX(fecha_vencimiento) FROM cuota WHERE id_cliente = ?",
            arrayOf(clienteId.toString())
        )
        val fecha = if (cursor.moveToFirst()) cursor.getLong(0) else 0L
        cursor.close()
        return fecha
    }

    fun registrarPagoActividad(
        cliente: Cliente,
        cantidad_creditos: Int
    ): Boolean {
        return try {
            val db = dbHelper.writableDatabase
            val dbLectura = dbHelper.readableDatabase
            val cursor: Cursor = dbLectura.rawQuery(
                "SELECT cantidad_creditos FROM credito_actividades WHERE id_cliente = ?",
                arrayOf(cliente.id.toString())
            )
            if (cursor.moveToFirst()) {
                val cantidadActual = cursor.getInt(0)
                val nuevaCantidad = cantidadActual + cantidad_creditos
                val valuesUpdate = ContentValues().apply {
                    put("cantidad_creditos", nuevaCantidad)
                }
                db.update("credito_actividades", valuesUpdate, "id_cliente = ?", arrayOf(cliente.id.toString()))
            } else {
                val values = ContentValues().apply {
                    put("id_cliente", cliente.id)
                    put("cantidad_creditos", cantidad_creditos)
                }
                val result = db.insert("credito_actividades", null, values)
                db.close()
                return result != -1L
            }
            cursor.close()
            db.close()
            true
        } catch (e: Exception) {
            Log.e("PaymentManager", "Error al registrar pago: ${e.message}")
            false
        }
    }
}
