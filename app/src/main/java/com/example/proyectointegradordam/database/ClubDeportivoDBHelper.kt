package com.example.proyectointegradordam.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.proyectointegradordam.models.Cliente
import com.example.proyectointegradordam.models.ClienteConVencimiento

class clubDeportivoDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "clubdeportivo.db"
        const val DATABASE_VERSION = 8
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE cliente (
                id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                email TEXT NOT NULL,
                telefono TEXT NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE cuota (
                id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha_pago LONG NOT NULL,
                fecha_vencimiento LONG NOT NULL,
                medio_pago TEXT NOT NULL,
                monto INTEGER NOT NULL,
                tipo_cuota INTEGER NOT NULL,
                plazo_cuota INTEGER DEFAULT 1,
                id_cliente INTEGER,
                FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
            )
        """)

        db.execSQL("""
            CREATE TABLE actividad (
                id_actividad INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                horario TEXT NOT NULL,
                dia TEXT NOT NULL,
                profesor TEXT NOT NULL,
                costo REAL NOT NULL,
                cupo INTEGER NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE usuario (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_usuario TEXT NOT NULL,
                nombre TEXT NOT NULL,
                telefono TEXT NOT NULL,
                pass_usuario TEXT NOT NULL,
                activo INTEGER DEFAULT 1
                )
        """)

        db.execSQL("""
            CREATE TABLE credito_actividades (
                id_credito INTEGER PRIMARY KEY AUTOINCREMENT,
                id_cliente INTEGER NOT NULL,
                cantidad_creditos INTEGER NOT NULL,
                FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
            )
        """)

        db.execSQL("""
            CREATE TABLE inscripcion_actividad (
                id_inscripcion INTEGER PRIMARY KEY AUTOINCREMENT,
                id_cliente INTEGER NOT NULL,
                id_actividad INTEGER NOT NULL,
                fecha_inscripcion TEXT NOT NULL,
                activo INTEGER DEFAULT 1,
                FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
                FOREIGN KEY (id_actividad) REFERENCES actividad(id_actividad),
                UNIQUE(id_cliente, id_actividad)
            )
        """)

        db.execSQL("INSERT INTO usuario (nombre_usuario, pass_usuario, activo, nombre, telefono) VALUES ('Test', '123456', 1, 'Test', '0000000000')")
        db.execSQL("INSERT INTO cliente (nombre, apellido, email, telefono) VALUES ('Juan', 'Pérez', 'juan@mail.com', '12345678')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS credito_actividades")
        db.execSQL("DROP TABLE IF EXISTS usuario")
        db.execSQL("DROP TABLE IF EXISTS actividad")
        db.execSQL("DROP TABLE IF EXISTS cuota")
        db.execSQL("DROP TABLE IF EXISTS cliente")
        db.execSQL("DROP TABLE IF EXISTS inscripcion_actividad")
        onCreate(db)
    }

    fun buscarClientePorNombre(texto: String): List<Cliente> {
        val db = readableDatabase
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

    fun actualizarDatosCliente(
        id: Int,
        email: String,
        telefono: String
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("email", email)
            put("telefono", telefono)
        }
        return db.update("cliente", values, "id_cliente = ?", arrayOf(id.toString()))
    }

    fun obtenerTodosLosClientes(): List<Cliente> {
        val db = readableDatabase
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
        val db = readableDatabase

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
                val vencimiento = cursor.getLong(cursor.getColumnIndexOrThrow("vencimiento"))

                lista.add(ClienteConVencimiento(nombre, apellido, telefono, vencimiento))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

}
