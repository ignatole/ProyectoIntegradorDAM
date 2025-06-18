package com.example.proyectointegradordam.view

import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectointegradordam.BaseActivity
import com.example.proyectointegradordam.R
import com.example.proyectointegradordam.adapters.ActivityAdapter
import com.example.proyectointegradordam.database.clubDeportivoDBHelper
import com.example.proyectointegradordam.databinding.ActivityActividadesBinding
import com.example.proyectointegradordam.databinding.ModalFormAssingshiftBinding
import com.example.proyectointegradordam.databinding.ModalFormNewactivityBinding
import com.example.proyectointegradordam.managers.ActivitiesManager


class ActividadesActivity : BaseActivity(), ActivityAdapter.OnActividadActualizadaListener {

    private lateinit var binding: ActivityActividadesBinding
    private lateinit var adapter: ActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityActividadesBinding.inflate(layoutInflater)
        setContentViewWithBinding(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()

        binding.btnAssingShift.setOnClickListener { showModalFormAssingShift() }
        binding.btnNewActivity.setOnClickListener { showModalFormNewActivity() }
    }

    private fun setupRecyclerView() {
        val activities = ActivitiesManager(this)
        val lista = activities.obtenerActividades()

        adapter = ActivityAdapter(lista, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun showModalFormAssingShift() {
        val dialog = Dialog(this)
        val modalBinding = ModalFormAssingshiftBinding.inflate(layoutInflater)
        dialog.setContentView(modalBinding.root)

        val anchoPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            400f,
            resources.displayMetrics
        ).toInt()

        dialog.window?.setLayout(
            anchoPx,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        modalBinding.closeAssingShift.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun actualizarRecyclerView() {
        val activities = ActivitiesManager(this)
        val lista = activities.obtenerActividades()
        adapter.actualizarLista(lista)
    }

    private fun showModalFormNewActivity() {
        val dialog = Dialog(this)
        val modalBinding = ModalFormNewactivityBinding.inflate(layoutInflater)
        dialog.setContentView(modalBinding.root)

        val activities = ActivitiesManager(this)

        modalBinding.closeNewActivity.setOnClickListener {
            dialog.dismiss()
        }

        val calendario = Calendar.getInstance()
        val formato = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        var fechaHoraSeleccionada = ""

        modalBinding.btnFechaHora.setOnClickListener {
            val datePicker = android.app.DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendario.set(Calendar.YEAR, year)
                    calendario.set(Calendar.MONTH, month)
                    calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val timePicker = android.app.TimePickerDialog(
                        this,
                        { _, hourOfDay, minute ->
                            calendario.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendario.set(Calendar.MINUTE, minute)

                            fechaHoraSeleccionada = formato.format(calendario.time)
                            modalBinding.btnFechaHora.text = fechaHoraSeleccionada
                        },
                        calendario.get(Calendar.HOUR_OF_DAY),
                        calendario.get(Calendar.MINUTE),
                        true
                    )
                    timePicker.show()
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        modalBinding.btnAddShift.setOnClickListener {
            val nombre = modalBinding.etNombreActividad.text.toString()
            val profesor = modalBinding.etProfesor.text.toString()
            val cupo = modalBinding.etCupo.text.toString().toIntOrNull() ?: 0
            val costo = modalBinding.etCosto.text.toString().toFloatOrNull() ?: 0f

            if (nombre.isNotBlank() && profesor.isNotBlank() && cupo > 0 && costo >= 0 && fechaHoraSeleccionada.isNotEmpty()) {
                val actividad = ActivitiesManager.Actividad(
                    nombre = nombre,
                    horario = fechaHoraSeleccionada,
                    dia = fechaHoraSeleccionada.split(" ")[0],
                    profesor = profesor,
                    costo = costo,
                    cupo = cupo
                )

                val success = activities.insertarActividad(actividad)

                if (success) {
                    Toast.makeText(this, "Actividad registrada", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    actualizarRecyclerView()
                } else {
                    Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Completá todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        val anchoPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            400f,
            resources.displayMetrics
        ).toInt()
        dialog.window?.setLayout(anchoPx, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }

    // Implementación de la interfaz
    override fun onActividadActualizada() {
        actualizarRecyclerView()
    }
}
