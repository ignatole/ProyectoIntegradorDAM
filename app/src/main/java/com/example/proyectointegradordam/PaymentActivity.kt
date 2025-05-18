package com.example.proyectointegradordam

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectointegradordam.databinding.ActivityPaymentBinding
import com.example.proyectointegradordam.databinding.ModalFormPaymentReceiptBinding

class PaymentActivity : BaseActivity() {
    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentViewWithBinding(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val searchEditText = binding.searchClient.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.black))

        binding.buttonPayment.setOnClickListener {
            val dialog = Dialog(this)
            val modalBinding = ModalFormPaymentReceiptBinding.inflate(LayoutInflater.from(this))
            dialog.setContentView(modalBinding.root)
            dialog.show()
        }
    }
}