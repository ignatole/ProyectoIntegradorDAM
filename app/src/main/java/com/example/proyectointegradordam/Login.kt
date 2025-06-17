package com.example.proyectointegradordam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectointegradordam.managers.UserManager
import com.example.proyectointegradordam.view.MenuActivity
import com.google.android.material.textfield.TextInputEditText

class Login : BaseActivity() { // Hereda de BaseActivity para tener acceso a db

    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userManager = UserManager(this)

        val emailAddressInput = findViewById<EditText>(R.id.emailAddressInput)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginBtn)

        loginButton.setOnClickListener {
            val username = emailAddressInput.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = userManager.getUserByCredentials(username, password)

            if (user != null) {
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun useDrawer(): Boolean = false
}
