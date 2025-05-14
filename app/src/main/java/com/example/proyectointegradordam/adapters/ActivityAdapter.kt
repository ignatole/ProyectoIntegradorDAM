package com.example.proyectointegradordam.adapters

import android.app.Dialog
import android.content.Context
import android.icu.util.Calendar
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannedString
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradordam.databinding.ItemActivityBinding
import com.example.proyectointegradordam.databinding.ModalConfirmDeleteBinding
import com.example.proyectointegradordam.databinding.ModalFormNewactivityBinding
import com.example.proyectointegradordam.view.Activities

class ActivityAdapter(private val lista: List<Activities>) :
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

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

    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activities = lista[position]

        //SUBRAYAR TEXTO
        val texto = activities.name
        val subrayado = SpannableString(texto)
        subrayado.setSpan(
            UnderlineSpan(),
            0,
            texto.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        holder.binding.tvActivity.text = subrayado
        holder.binding.tvDateTime.text = activities.date
        holder.binding.tvCupo.text = "2 / ${activities.cupo}"

        holder.binding.btnEditItem.setOnClickListener { showModalFormEdit(holder.itemView.context) }
        holder.binding.btnDeleteItem.setOnClickListener { showDeleteConfirmation(holder.itemView.context) }
    }

    //MODAL PARA EDITAR (REUTILIZANDO EL ModalFormNewActivity)
    private fun showModalFormEdit(context: Context) {
        val dialog = Dialog(context)
        val modalBinding = ModalFormNewactivityBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(modalBinding.root)

        //ESTABLECER EL ANCHO DEL MODAL
        val anchoPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            400f,
            context.resources.displayMetrics
        ).toInt()

        dialog.window?.setLayout(
            anchoPx,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        //ONCLICK DEL BOTON X PARA CERRAR EL MODAL
        modalBinding.closeNewActivity.setOnClickListener { dialog.dismiss() }


        //CALENDARIO Y RELOJ
        val calendario = Calendar.getInstance()

        modalBinding.btnFechaHora.setOnClickListener {
            val datePicker = android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendario.set(Calendar.YEAR, year)
                    calendario.set(Calendar.MONTH, month)
                    calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    // Selector de hora
                    val timePicker = android.app.TimePickerDialog(
                        context,
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
        dialog.show()
    }

    //MODAL ELIMINAR ITEM
    private fun showDeleteConfirmation(context: Context) {
        val dialog = Dialog(context)
        val modalBinding = ModalConfirmDeleteBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(modalBinding.root)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        modalBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        modalBinding.btnDelete.setOnClickListener { dialog.dismiss() }

        dialog.show()

    }

}