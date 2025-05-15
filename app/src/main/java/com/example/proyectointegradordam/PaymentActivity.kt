package com.example.proyectointegradordam

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectointegradordam.databinding.ActivityPaymentBinding
import com.example.proyectointegradordam.databinding.ModalFormPaymentReceiptBinding

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonPayment.setOnClickListener{
            val dialog=Dialog(this)
            val modalBinding = ModalFormPaymentReceiptBinding.inflate(LayoutInflater.from(this))
            dialog.setContentView(modalBinding.root)
            dialog.show()

        }

    }

}