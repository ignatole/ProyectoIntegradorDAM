package com.example.proyectointegradordam

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradordam.adapters.ClienteAdapter
import com.example.proyectointegradordam.managers.ClienteManager
import com.example.proyectointegradordam.managers.PaymentManager
import com.example.proyectointegradordam.models.Cliente
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.appcompat.widget.SearchView

class PaymentActivity : BaseActivity() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerClientes: RecyclerView
    private lateinit var adapterClientes: ClienteAdapter
    private lateinit var txtClienteSeleccionado: TextView
    private lateinit var groupFormularioPago: LinearLayout
    private lateinit var inputMonto: EditText
    private lateinit var inputCantidad: EditText
    private lateinit var radioEfectivo: RadioButton
    private lateinit var radioTarjeta: RadioButton
    private lateinit var radioCuota: RadioButton
    private lateinit var radioCreditos: RadioButton
    private lateinit var spinnerCuotas: Spinner
    private lateinit var botonPagar: ImageView

    private var clienteSeleccionado: Cliente? = null

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchView = findViewById(R.id.searchClient)
        recyclerClientes = findViewById(R.id.recyclerClientes)
        txtClienteSeleccionado = findViewById(R.id.tvClienteSeleccionado)
        groupFormularioPago = findViewById(R.id.groupFormularioPago)
        inputMonto = findViewById(R.id.inputMonto)
        inputCantidad = findViewById(R.id.inputCantidad)
        radioEfectivo = findViewById(R.id.radioEfectivo)
        radioTarjeta = findViewById(R.id.radioTarjeta)
        radioCuota = findViewById(R.id.radioCuota)
        radioCreditos = findViewById(R.id.radioCreditos)
        spinnerCuotas = findViewById(R.id.spinnerCuotas)
        botonPagar = findViewById(R.id.button_payment)

        searchView.setIconifiedByDefault(false)
        searchView.isIconified = false

        configurarSpinnerCuotas()
        configurarListeners()

        botonPagar.setOnClickListener { registrarPago() }

        adapterClientes = ClienteAdapter(emptyList()) { cliente ->
            clienteSeleccionado = cliente
            txtClienteSeleccionado.text = getString(R.string.Cliente, cliente.nombre, cliente.apellido)
            groupFormularioPago.visibility = View.VISIBLE
            searchView.clearFocus()
            searchView.setQuery("", true)
            recyclerClientes.visibility = View.GONE
        }

        recyclerClientes.layoutManager = LinearLayoutManager(this)
        recyclerClientes.adapter = adapterClientes

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                buscarClientes(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                buscarClientes(newText.orEmpty())
                return true
            }
        })
    }

    private fun buscarClientes(texto: String) {
        val clienteManager = ClienteManager(this)
        val clientes = clienteManager.buscarClientePorNombre(texto)
        adapterClientes.updateData(clientes)
        recyclerClientes.visibility = if (clientes.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun configurarSpinnerCuotas() {
        val cuotas = listOf("1 cuota", "3 cuotas", "6 cuotas")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cuotas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCuotas.adapter = adapter
    }

    private fun configurarListeners() {
        val actualizarFormulario = {
            val esCuota = radioCuota.isChecked
            val esCredito = radioCreditos.isChecked
            val conTarjeta = radioTarjeta.isChecked

            spinnerCuotas.visibility = if (conTarjeta) View.VISIBLE else View.GONE
            inputCantidad.visibility = if (esCredito) View.VISIBLE else View.GONE

            if (esCuota) {
                inputMonto.setText("30000")
                inputMonto.isEnabled = false
            } else if (esCredito) {
                val cantidad = inputCantidad.text.toString().toIntOrNull() ?: 1
                val monto = cantidad.coerceIn(1, 10) * 5000
                inputMonto.setText(monto.toString())
                inputMonto.isEnabled = false
            }
        }

        radioCuota.setOnCheckedChangeListener { _, _ -> actualizarFormulario() }
        radioCreditos.setOnCheckedChangeListener { _, _ -> actualizarFormulario() }
        radioTarjeta.setOnCheckedChangeListener { _, _ -> actualizarFormulario() }
        radioEfectivo.setOnCheckedChangeListener { _, _ -> actualizarFormulario() }

        inputCantidad.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) actualizarFormulario()
        }
    }

    private fun registrarPago() {
        val cliente = clienteSeleccionado
        if (cliente == null) {
            Toast.makeText(this, "Seleccioná un cliente primero", Toast.LENGTH_SHORT).show()
            return
        }

        val montoText = inputMonto.text.toString()
        if (montoText.isEmpty()) {
            Toast.makeText(this, "Completá el monto", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoText.toDoubleOrNull()
        if (monto == null) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val tipoPago = if (radioCuota.isChecked) "mensual" else "actividad"
        val medioPago = if (radioEfectivo.isChecked) "efectivo" else "tarjeta"

        val cuotasSeleccionadas = when (spinnerCuotas.selectedItemPosition) {
            0 -> 1
            1 -> 3
            2 -> 6
            else -> 1
        }

        val paymentManager = PaymentManager(this)
        var (exito, nuevaFecha) = paymentManager.registrarPagoCuota(
            cliente = cliente,
            monto = monto,
            medioPago = medioPago,
            tipo = tipoPago,
            plazo = cuotasSeleccionadas
        )

        if (tipoPago == "mensual") {
            emitirRecibo(cliente, medioPago, SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(nuevaFecha)), 0, true)
        } else {
            val cantCreditos = inputCantidad.text.toString().toIntOrNull() ?: 1
            exito = paymentManager.registrarPagoActividad(
                cliente = cliente,
                cantidad_creditos = cantCreditos
            )
            emitirRecibo(cliente, medioPago, SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(nuevaFecha)), cantCreditos, false)

        }

        if (exito) {
            Toast.makeText(this, "Pago registrado con éxito", Toast.LENGTH_SHORT).show()
            limpiarFormulario()
        } else {
            Toast.makeText(this, "Error al registrar el pago", Toast.LENGTH_SHORT).show()
        }
    }

    private fun emitirRecibo(cliente: Cliente, metodo: String, vencimiento: String, cantCreditos: Int,  cuota: Boolean) {
        val dialogView = layoutInflater.inflate(R.layout.modal_form_payment_receipt, null)

        val tvNombreCliente = dialogView.findViewById<TextView>(R.id.tvNombreCliente)
        val tvNumeroSocio = dialogView.findViewById<TextView>(R.id.tvNumeroSocio)
        val tvVencimiento = dialogView.findViewById<TextView>(R.id.tvVencimiento)
        val tvMetodoPagoModal = dialogView.findViewById<TextView>(R.id.tvMetodoPagoModal)
        val tvCantCreditos = dialogView.findViewById<TextView>(R.id.tvCantCreditos)

        if (cuota) {
            tvCantCreditos.visibility = View.GONE
        } else {
            tvCantCreditos.text = "Cant. creditos: $cantCreditos"
        }

        tvNombreCliente.text = "${cliente.nombre} ${cliente.apellido}"
        tvNumeroSocio.text = "Socio N°${cliente.id}"
        tvVencimiento.text = "Vencimiento: $vencimiento"
        tvMetodoPagoModal.text = "Medio de pago: $metodo"

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.show()
    }
    private val radioGroupMedioPago by lazy { findViewById<RadioGroup>(R.id.grupoFormaPago) }
    private val radioGroupTipoPago by lazy { findViewById<RadioGroup>(R.id.grupoTipoCompra) }

    private fun limpiarFormulario() {
        clienteSeleccionado = null
        txtClienteSeleccionado.text = getString(R.string.cliente_sin_seleccionar)
        groupFormularioPago.visibility = View.GONE
        inputMonto.setText("")
        inputCantidad.setText("")
        radioGroupTipoPago.clearCheck()
        radioGroupMedioPago.clearCheck()
        spinnerCuotas.setSelection(0)
    }
}
