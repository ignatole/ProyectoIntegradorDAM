package com.example.proyectointegradordam

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectointegradordam.database.clubDeportivoDBHelper

class RegistroSocio : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_socio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etTelefono = findViewById<EditText>(R.id.etTelefono)
        val btnRegistroSocio = findViewById<Button>(R.id.btnRegistroSocio)

        val dbHelper = clubDeportivoDBHelper(this)

        btnRegistroSocio.setOnClickListener{
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()

            if(nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || telefono.isEmpty()){
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            } else {
                val db = dbHelper.writableDatabase
                val sql = """
                    INSERT INTO cliente (nombre, apellido, email, telefono)
                    VALUES (?, ?, ?, ?)
                """.trimIndent()
                val statement = db.compileStatement(sql)
                statement.bindString(1, nombre)
                statement.bindString(2, apellido)
                statement.bindString(3, email)
                statement.bindString(4, telefono)

                val rowId = statement.executeInsert()

                // para ver si se registra correctament
                Log.d("SQL", "Id insertado: $rowId")
                if(rowId != -1L) {
                    Toast.makeText(this, "Socio registrado con éxito", Toast.LENGTH_SHORT).show()
                    etNombre.text.clear()
                    etApellido.text.clear()
                    etEmail.text.clear()
                    etTelefono.text.clear()
                } else {
                    Toast.makeText(this, "Error al registrar socio", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
