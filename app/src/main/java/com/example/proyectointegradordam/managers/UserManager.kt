package com.example.proyectointegradordam.managers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.proyectointegradordam.database.clubDeportivoDBHelper
import com.example.proyectointegradordam.models.User

class UserManager(private val context: Context) {

    private val dbHelper = clubDeportivoDBHelper(context)

    fun insertUser(username: String, password: String, isActive: Boolean = true): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre_usuario", username)
            put("pass_usuario", password)
            put("activo", if (isActive) 1 else 0)
        }
        return db.insert("usuario", null, values)
    }

    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM usuario", null)

        if (cursor.moveToFirst()) {
            do {
                val user = User(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                    username = cursor.getString(cursor.getColumnIndexOrThrow("nombre_usuario")),
                    password = cursor.getString(cursor.getColumnIndexOrThrow("pass_usuario")),
                    isActive = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1
                )
                userList.add(user)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return userList
    }

    fun getUserByCredentials(username: String, password: String): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuario WHERE nombre_usuario = ? AND pass_usuario = ? AND activo = 1",
            arrayOf(username, password)
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                username = cursor.getString(cursor.getColumnIndexOrThrow("nombre_usuario")),
                password = cursor.getString(cursor.getColumnIndexOrThrow("pass_usuario")),
                isActive = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1
            )
        }

        cursor.close()
        return user
    }
}
