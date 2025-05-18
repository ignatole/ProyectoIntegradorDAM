package com.example.proyectointegradordam

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.proyectointegradordam.view.ActividadesActivity
import com.google.android.material.navigation.NavigationView

open class BaseActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun setContentView(layoutResID: Int) {
        val fullView = layoutInflater.inflate(R.layout.activity_base, null)
        val contentFrame = fullView.findViewById<FrameLayout>(R.id.content_frame)
        layoutInflater.inflate(layoutResID, contentFrame, true)
        super.setContentView(fullView)

        setupDrawer(fullView)
        setupBackDispatcher()
    }

    fun setContentViewWithBinding(view: View) {
        val fullView = layoutInflater.inflate(R.layout.activity_base, null)
        val contentFrame = fullView.findViewById<FrameLayout>(R.id.content_frame)
        contentFrame.addView(view)
        super.setContentView(fullView)

        setupDrawer(fullView)
        setupBackDispatcher()
    }

    private fun setupDrawer(fullView: View) {
        drawerLayout = fullView.findViewById(R.id.drawer_layout)
        navView = fullView.findViewById(R.id.nav_view)

        val menuItemsView = fullView.findViewById<View>(R.id.nav_menu_items)

        highlightMenuIfCurrent(
            menuItemsView.findViewById(R.id.btn_cobros),
            PaymentActivity::class.java
        )

        highlightMenuIfCurrent(
            menuItemsView.findViewById(R.id.btn_actividades),
            ActividadesActivity::class.java
        )

        highlightMenuIfCurrent(
            menuItemsView.findViewById(R.id.btn_registro),
            RegistroSocio::class.java
        )

        val logoutButton = fullView.findViewById<Button>(R.id.btn_logout)

        logoutButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("¿Cerrar sesión?")
                .setMessage("¿Estás seguro que quieres cerar sesión?")
                .setPositiveButton("Sí") { _, _ ->
                    val intent = Intent(this, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("No", null)
                .show()
        }

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar?>(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }

    private fun setupBackDispatcher() {
        onBackPressedDispatcher.addCallback(this) {
            if (::drawerLayout.isInitialized && drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    fun navigateFromDrawer(intent: Intent) {
        drawerLayout.closeDrawer(GravityCompat.END)
        drawerLayout.postDelayed({
            startActivity(intent)
        }, 200)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.menu_drawer -> {
                drawerLayout.openDrawer(GravityCompat.END)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    private fun highlightMenuIfCurrent(
        button: View?,
        targetClass: Class<*>,
        highlightColorRes: Int = R.color.selected_menu_item
    ) {
        if (this::class.java == targetClass) {
            button?.setBackgroundColor(getColor(highlightColorRes))
            button?.isEnabled = false
        } else {
            button?.setOnClickListener {
                navigateFromDrawer(Intent(this, targetClass))
            }
        }
    }
}
