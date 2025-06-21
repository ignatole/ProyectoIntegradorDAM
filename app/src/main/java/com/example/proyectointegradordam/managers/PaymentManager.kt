package com.example.proyectointegradordam.managers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.example.proyectointegradordam.database.clubDeportivoDBHelper
import com.example.proyectointegradordam.models.Cliente
import java.io.Serializable

class PaymentManager(private val context: Context) {

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
            // Ahora la fecha de pago se registra en numero (double)
            val fechaPago = System.currentTimeMillis()

            // Insertar pago, tengo que ver acá si se registra bien el pago
            val values = ContentValues().apply {
                put("fecha_pago", fechaPago)
                put("medio_pago", medioPago)
                put("monto", monto)
                put("tipo_cuota", if (tipo == "mensual") 1 else 2)
                put("plazo_cuota", plazo)
                put("id_cliente", cliente.id)
            }
            val result = db.insert("cuota", null, values)
            var nuevaFechaVencimiento: Long = 0
            val dbLectura = dbHelper.readableDatabase
            val cursor: Cursor = dbLectura.rawQuery(
                "SELECT fecha_vencimiento FROM cliente WHERE id_cliente = ?",
                arrayOf(cliente.id.toString())
            )
            // Actualizar fecha_vencimiento si es cuota mensual,
            // la onda es que si fecha pago es null solo se suman 30 dias a esa para
            // establecer fecha vencimiento, si se compra un credito tmb estaria bueno que
            // pueda vencerse, pero solo 30 dias posteriores a la ultima compra
            // hay que manejar cuando tiene o no cuota? LRPM
            if (tipo == "mensual") {
                nuevaFechaVencimiento = if (cursor.moveToFirst()) {
                    val fechaActual = cursor.getLong(0)
                    if (fechaActual < fechaPago) {
                        fechaPago + 30L * 24 * 60 * 60 * 1000 // Nueva base si ya venció
                    } else {
                        fechaActual + 30L * 24 * 60 * 60 * 1000 // Extensión desde la fecha actual
                    }
                } else {
                    fechaPago + 30L * 24 * 60 * 60 * 1000
                }
            } else {
                nuevaFechaVencimiento = fechaPago + 30L * 24 * 60 * 60 * 1000

                cursor.close()

                val valuesUpdate = ContentValues().apply {
                    put("fecha_vencimiento", nuevaFechaVencimiento)
                }

                db.update("cliente", valuesUpdate, "id_cliente = ?", arrayOf(cliente.id.toString()))
            }

            db.close()
            return Pair(true, nuevaFechaVencimiento)
        } catch (e: Exception) {
            Log.e("PaymentManager", "Error al registrar pago: ${e.message}")
            return Pair(false, 0L)
        }
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
                result != -1L
                }
            true
        } catch (e: Exception) {
            Log.e("PaymentManager", "Error al registrar pago: ${e.message}")
            false
        }
    }
}
