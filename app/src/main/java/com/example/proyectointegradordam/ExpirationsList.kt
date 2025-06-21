package com.example.proyectointegradordam

import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradordam.adapters.ExpirationsAdapter
import com.example.proyectointegradordam.database.clubDeportivoDBHelper
import com.example.proyectointegradordam.models.ClienteConVencimiento

class ExpirationsList : BaseActivity() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpirationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expirations_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recycler_expiration)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dbHelper = clubDeportivoDBHelper(this)
        val lista: List<ClienteConVencimiento> = dbHelper.obtenerClientesConVencimientos()

        adapter = ExpirationsAdapter(lista)
        recyclerView.adapter = adapter
    }
}
