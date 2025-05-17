package com.example.proyectointegradordam.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectointegradordam.ExpirationsList
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

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

            val dialog = androidx.appcompat.app.AlertDialog.Builder(this).setView(dialogView).create()

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