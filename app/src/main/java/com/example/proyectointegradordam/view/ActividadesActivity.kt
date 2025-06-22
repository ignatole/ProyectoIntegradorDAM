package com.example.proyectointegradordam.view

import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
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
import com.example.proyectointegradordam.managers.InscripcionManager
import com.example.proyectointegradordam.models.Activities
import com.example.proyectointegradordam.models.Cliente
import com.example.proyectointegradordam.managers.EditClienteManager

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

        val dbHelper = clubDeportivoDBHelper(this)
        val inscripcionManager = InscripcionManager(this)
        val editClienteManager = EditClienteManager(this)

        var clienteSeleccionado: Cliente? = null
        var actividadSeleccionada: Activities? = null

        // Obtener todos los clientes
        val todosLosClientes = editClienteManager.obtenerTodosLosClientes()
        val spinnerClientes = modalBinding.root.findViewById<Spinner>(R.id.spinnerClientes)
        val spinnerActividades = modalBinding.root.findViewById<Spinner>(R.id.spinnerActividades)

        // Configurar el Spinner de clientes
        val nombresClientes = listOf("Seleccionar cliente...") + todosLosClientes.map { "${it.nombre} ${it.apellido}" }
        val adapterClientes = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresClientes)
        adapterClientes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClientes.adapter = adapterClientes

        spinnerClientes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                clienteSeleccionado = if (position == 0) null else todosLosClientes[position - 1]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                clienteSeleccionado = null
            }
        }

        // Obtener actividades con cupo
        val actividadesConCupo = inscripcionManager.obtenerActividadesConCupo()

        // Configurar el Spinner de actividades
        val nombresActividades = listOf("Seleccionar actividad...") +
                actividadesConCupo.map { "${it.nombre} - Cupos: ${it.cupo} - ${it.dia}" }
        val adapterActividades = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresActividades)
        adapterActividades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActividades.adapter = adapterActividades

        spinnerActividades.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                actividadSeleccionada = if (position == 0) null else actividadesConCupo[position - 1]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                actividadSeleccionada = null
            }
        }

        modalBinding.closeAssingShift.setOnClickListener { dialog.dismiss() }

        modalBinding.btnAddShift.setOnClickListener {
            if (clienteSeleccionado == null) {
                Toast.makeText(this, "Selecciona un cliente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (actividadSeleccionada == null) {
                Toast.makeText(this, "Selecciona una actividad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Realizar la inscripción
            val (success, mensaje) = inscripcionManager.inscribirClienteEnActividad(
                clienteSeleccionado!!.id,
                actividadSeleccionada!!.id!!
            )

            if (success) {
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                actualizarRecyclerView() // Para reflejar el cambio en los cupos
            } else {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
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

    private fun actualizarRecyclerView() {
        val activities = ActivitiesManager(this)
        val lista = activities.obtenerActividades()
        adapter.actualizarLista(lista)
    }

    private fun showModalFormNewActivity() {
        val dialog = Dialog(this)
        val modalBinding = ModalFormNewactivityBinding.inflate(layoutInflater)
        dialog.setContentView(modalBinding.root)

        val activitiesManager = ActivitiesManager(this)

        modalBinding.closeNewActivity.setOnClickListener { dialog.dismiss() }

        val calendario = Calendar.getInstance()
        val formato = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        var fechaHoraSeleccionada = ""

        modalBinding.btnFechaHora.setOnClickListener {
            val datePicker = android.app.DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendario.set(year, month, dayOfMonth)

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
                val actividad = Activities(
                    nombre = nombre,
                    horario = fechaHoraSeleccionada,
                    dia = fechaHoraSeleccionada.split(" ")[0],
                    profesor = profesor,
                    costo = costo,
                    cupo = cupo
                )

                val success = activitiesManager.insertarActividad(actividad)

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

    override fun onActividadActualizada() {
        actualizarRecyclerView()
    }
}