package com.example.proyectointegradordam.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.proyectointegradordam.models.Cliente

class clubDeportivoDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "clubdeportivo.db"
        const val DATABASE_VERSION = 4
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
                fecha_pago TEXT NOT NULL,
                fecha_vencimiento TEXT,
                medio_pago TEXT NOT NULL,
                monto REAL NOT NULL,
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

        // Datos iniciales
        db.execSQL("INSERT INTO usuario (nombre_usuario, pass_usuario, activo, nombre, telefono) VALUES ('Test', '123456', 1, 'Test', '0000000000')")


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


}
