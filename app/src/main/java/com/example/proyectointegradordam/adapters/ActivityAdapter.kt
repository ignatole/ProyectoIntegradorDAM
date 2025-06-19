package com.example.proyectointegradordam.adapters

import android.app.Dialog
import android.content.Context
import android.icu.util.Calendar
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradordam.databinding.ItemActivityBinding
import com.example.proyectointegradordam.databinding.ModalConfirmDeleteBinding
import com.example.proyectointegradordam.databinding.ModalFormNewactivityBinding
import com.example.proyectointegradordam.managers.ActivitiesManager
import com.example.proyectointegradordam.models.Activities

class ActivityAdapter(
    private var lista: List<Activities>,
    private val listener: OnActividadActualizadaListener
) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    inner class ActivityViewHolder(val binding: ItemActivityBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding = ItemActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActivityViewHolder(binding)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val actividad = lista[position]

        // Subrayar el nombre
        val subrayado = SpannableString(actividad.nombre).apply {
            setSpan(
                UnderlineSpan(),
                0,
                actividad.nombre.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        holder.binding.tvActivity.text = subrayado
        holder.binding.tvProfesor.text = actividad.profesor
        holder.binding.tvDateTime.text = actividad.dia
        holder.binding.tvCupo.text = actividad.cupo.toString()

        holder.binding.btnEditItem.setOnClickListener {
            showModalFormEdit(holder.itemView.context, actividad)
        }

        holder.binding.btnDeleteItem.setOnClickListener {
            showDeleteConfirmation(holder.itemView.context, actividad)
        }
    }

    private fun showModalFormEdit(context: Context, actividad: Activities) {
        val dialog = Dialog(context)
        val modalBinding = ModalFormNewactivityBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(modalBinding.root)

        val activitiesManager = ActivitiesManager(context)
        val calendario = Calendar.getInstance()
        var fechaHoraSeleccionada = actividad.horario

        // Campos prellenados
        modalBinding.etNombreActividad.setText(actividad.nombre)
        modalBinding.etProfesor.setText(actividad.profesor)
        modalBinding.etCosto.setText(actividad.costo.toString())
        modalBinding.etCupo.setText(actividad.cupo.toString())
        modalBinding.btnFechaHora.text = actividad.horario

        modalBinding.btnFechaHora.setOnClickListener {
            val datePicker = android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendario.set(year, month, dayOfMonth)

                    val timePicker = android.app.TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            calendario.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendario.set(Calendar.MINUTE, minute)

                            val formato = java.text.SimpleDateFormat(
                                "dd/MM/yyyy HH:mm",
                                java.util.Locale.getDefault()
                            )
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

        modalBinding.btnAddShift.text = "Actualizar actividad"
        modalBinding.btnAddShift.setOnClickListener {
            val nombre = modalBinding.etNombreActividad.text.toString()
            val profesor = modalBinding.etProfesor.text.toString()
            val cupo = modalBinding.etCupo.text.toString().toIntOrNull() ?: 0
            val costo = modalBinding.etCosto.text.toString().toFloatOrNull() ?: 0f

            if (nombre.isNotBlank() && profesor.isNotBlank() && cupo > 0 && costo >= 0 && fechaHoraSeleccionada.isNotEmpty()) {
                val nuevaActividad = Activities(
                    id = actividad.id,
                    nombre = nombre,
                    profesor = profesor,
                    horario = fechaHoraSeleccionada,
                    dia = fechaHoraSeleccionada.split(" ")[0],
                    costo = costo,
                    cupo = cupo
                )

                val success = activitiesManager.actualizarActividad(nuevaActividad)

                if (success) {
                    Toast.makeText(context, "Actividad actualizada", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    listener.onActividadActualizada()
                } else {
                    Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Completá todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        modalBinding.closeNewActivity.setOnClickListener { dialog.dismiss() }

        dialog.window?.setLayout(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                400f,
                context.resources.displayMetrics
            ).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }

    private fun showDeleteConfirmation(context: Context, actividad: Activities) {
        val dialog = Dialog(context)
        val modalBinding = ModalConfirmDeleteBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(modalBinding.root)

        modalBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        modalBinding.btnDelete.setOnClickListener {
            val activitiesManager = ActivitiesManager(context)
            val success = activitiesManager.eliminarActividad(actividad.id!!)

            if (success) {
                Toast.makeText(context, "Actividad eliminada", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                listener.onActividadActualizada()
            } else {
                Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }

    fun actualizarLista(nuevaLista: List<Activities>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }

    interface OnActividadActualizadaListener {
        fun onActividadActualizada()
    }
}