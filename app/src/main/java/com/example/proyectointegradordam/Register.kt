package com.example.proyectointegradordam

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectointegradordam.view.MenuActivity
import com.example.proyectointegradordam.managers.UserManager
import com.google.android.material.textfield.TextInputLayout

class Register : AppCompatActivity() {

    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userManager = UserManager(this)

        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail) // NUEVO
        val editTextPhone = findViewById<EditText>(R.id.editTextPhone)

        val textInputPassLayout = findViewById<TextInputLayout>(R.id.textInputPass)
        val textInputPass = textInputPassLayout.editText

        val textInputRepeatPassLayout = findViewById<TextInputLayout>(R.id.textInputRepeatPassword)
        val textInputRepeatPassword = textInputRepeatPassLayout.editText

        val checkBox = findViewById<CheckBox>(R.id.checkBox)
        val btnNext = findViewById<Button>(R.id.btnNext)

        btnNext.setOnClickListener {
            val name = editTextName.text.toString()
            val email = editTextEmail.text.toString() // NUEVO
            val phone = editTextPhone.text.toString()
            val pass = textInputPass?.text.toString()
            val repeatPass = textInputRepeatPassword?.text.toString()

            if (name.isBlank() || email.isBlank() || phone.isBlank() || pass.isBlank() || repeatPass.isBlank()) {
                Toast.makeText(this, "Por favor completá todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass != repeatPass) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!checkBox.isChecked) {
                Toast.makeText(this, "Debés aceptar los términos y condiciones", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = userManager.insertUser(email, pass, name, phone) // email como username

            if (result != -1L) {
                Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
