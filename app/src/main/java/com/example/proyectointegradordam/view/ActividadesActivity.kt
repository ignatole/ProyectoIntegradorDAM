package com.example.proyectointegradordam.view

import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectointegradordam.R
import com.example.proyectointegradordam.adapters.ActivityAdapter
import com.example.proyectointegradordam.databinding.ActivityActividadesBinding
import com.example.proyectointegradordam.databinding.ModalFormNewactivityBinding

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

        val activities = listOf(
            Activities("Yoga", "2025-05-10 10:00", "15"),
            Activities("Fútbol", "2025-05-11 17:30", "22"),
            Activities("Natación", "2025-05-12 14:00", "10"),
            Activities("Calistenia", "2025-05-12 14:00", "10"),
            Activities("Pesas", "2025-05-12 14:00", "10"),
            Activities("Crossfit", "2025-05-12 14:00", "10")



        )

        binding.btnAssingShift.setOnClickListener { showModalFormAssingShift() }
        binding.btnNewActivity.setOnClickListener { showModalFormNewActivity() }

        val adapter = ActivityAdapter(activities)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun showModalFormAssingShift() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.modal_form_assingshift)

        dialog.show()
    }

    private fun showModalFormNewActivity() {
        val dialog = Dialog(this)
        val modalBinding = ModalFormNewactivityBinding.inflate(layoutInflater)

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
        dialog.show()

        val calendario = Calendar.getInstance()

        modalBinding.btnFechaHora.setOnClickListener {
            val datePicker = android.app.DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendario.set(Calendar.YEAR, year)
                    calendario.set(Calendar.MONTH, month)
                    calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    // Selector de hora
                    val timePicker = android.app.TimePickerDialog(
                        this,
                        { _, hourOfDay, minute ->
                            calendario.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendario.set(Calendar.MINUTE, minute)

                            val formato = java.text.SimpleDateFormat(
                                "dd/MM/yyyy HH:mm",
                                java.util.Locale.getDefault()
                            )
                            val fechaHora = formato.format(calendario.time)

                            modalBinding.btnFechaHora.text = fechaHora
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

        //dialog.show()
    }
}