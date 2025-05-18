package com.example.proyectointegradordam.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectointegradordam.ExpirationsList
import com.example.proyectointegradordam.Login
import com.example.proyectointegradordam.MemberEdit
import com.example.proyectointegradordam.PaymentActivity
import com.example.proyectointegradordam.R
import com.example.proyectointegradordam.RegistroSocio
import com.example.proyectointegradordam.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this) {}

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button = findViewById<Button>(R.id.btn_logout)

        button.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("¿Cerrar sesión?")
                .setMessage("¿Estás seguro que quieres cerar sesión?")
                .setPositiveButton("Sí") { _, _ ->
                    val intent = Intent(this, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("No", null)
                .show()
        }

        binding.cvActivities.setOnClickListener{
            val intent = Intent(this, ActividadesActivity::class.java)
            startActivity(intent)
        }

        binding.cvCobros.setOnClickListener{
            val intent = Intent(this, PaymentActivity::class.java)
            startActivity(intent)
        }

        binding.cardMembers.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.modal_cliente, null)

            val btnRegistrar = dialogView.findViewById<CardView>(R.id.card_registro)
            val btnEditar = dialogView.findViewById<CardView>(R.id.card_edition)

            val dialog = AlertDialog.Builder(this).setView(dialogView).create()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            btnRegistrar.setOnClickListener {
                startActivity(Intent(this, RegistroSocio ::class.java))
                dialog.dismiss()
            }
            btnEditar.setOnClickListener {
                startActivity(Intent(this, MemberEdit::class.java))
                dialog.dismiss()
            }
            dialog.show()
        }
        binding.cardExpirations.setOnClickListener{
            val intent = Intent(this, ExpirationsList::class.java)
            startActivity(intent)

        }

    }
}