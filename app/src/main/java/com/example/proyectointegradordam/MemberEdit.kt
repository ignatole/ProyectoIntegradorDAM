package com.example.proyectointegradordam

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradordam.adapters.ClienteAdapter
import com.example.proyectointegradordam.database.clubDeportivoDBHelper
import com.example.proyectointegradordam.managers.EditClienteManager

class MemberEdit : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_member_edit)

        val editClienteManager = EditClienteManager(this)
        val etIdCliente = findViewById<EditText>(R.id.etIdCliente)
        val etNombreEdit = findViewById<EditText>(R.id.etNombreEdit)
        val etApellidoEdit = findViewById<EditText>(R.id.etApellidoEdit)
        val etEmailEdit = findViewById<EditText>(R.id.etEmailEdit)
        val etTelefonoEdit = findViewById<EditText>(R.id.etTelefonoEdit)
        val recyclerView = findViewById<RecyclerView>(R.id.rvResultados)

        val dbHelper = clubDeportivoDBHelper(this)
        val adapter = ClienteAdapter { cliente ->
            etIdCliente.setText(cliente.id.toString())
            etNombreEdit.setText(cliente.nombre)
            etApellidoEdit.setText(cliente.apellido)
            etEmailEdit.setText(cliente.email)
            etTelefonoEdit.setText(cliente.telefono)

            recyclerView.visibility = View.GONE

        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val editTextBuscar = findViewById<EditText>(R.id.editText)
        editTextBuscar.addTextChangedListener(object : android.text.TextWatcher{
            override fun afterTextChanged(s: android.text.Editable?){

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){
                val texto = s.toString()
                val resultados = editClienteManager.buscarClientePorNombre(texto)
                if(resultados.isNotEmpty()){
                    recyclerView.visibility = View.VISIBLE
                    adapter.updateData(resultados)
                } else {
                    recyclerView.visibility = View.GONE
                }
            }
        })

        val btnActualizar = findViewById<Button>(R.id.btnActualizar)
        btnActualizar.setOnClickListener{
            val id = etIdCliente.text.toString().toIntOrNull()
            val email = etEmailEdit.text.toString().trim()
            val telefono = etTelefonoEdit.text.toString().trim()

            if(id == null || email.isEmpty() || telefono.isEmpty()){
                Toast.makeText(this, "Completá el email y teléfono", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val filasActualizadas = editClienteManager.actualizarDatosCliente(id, email, telefono)
            if (filasActualizadas > 0){
                Toast.makeText(this, "Datos actualizados con éxito", Toast.LENGTH_SHORT).show()
                etIdCliente.setText("")
                etNombreEdit.setText("")
                etApellidoEdit.setText("")
                etEmailEdit.setText("")
                etTelefonoEdit.setText("")
            } else {
                Toast.makeText(this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show()
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}