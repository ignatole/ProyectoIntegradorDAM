package com.example.proyectointegradordam

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradordam.adapters.ExpirationAdapter

class ExpirationsList : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expirations_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_expiration)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // lista de pruebas
        val listaPrueba = listOf(
            ExpiredMember("Juan López", "45.668.123", "12/05/2025"),
            ExpiredMember("Maria Gómez", "25.789.987", "15/04/2025"),
            ExpiredMember("Federico Gutierrez", "38.456.656", "01/05/2024")
        )
        recyclerView.adapter = ExpirationAdapter(listaPrueba)
    }
}