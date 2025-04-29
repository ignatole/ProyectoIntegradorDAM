package com.example.proyectointegradordam.view

import android.app.Dialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectointegradordam.R
import com.example.proyectointegradordam.databinding.ActivityActividadesBinding

class ActividadesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActividadesBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityActividadesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnAssingShift.setOnClickListener{showModalForm()}
    }

    private fun showModalForm(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.modal_form_assingshift)
        dialog.show()
    }
}