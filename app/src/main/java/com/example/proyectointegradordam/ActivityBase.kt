package com.example.proyectointegradordam
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
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

        drawerLayout = fullView.findViewById(R.id.drawer_layout)
        navView = fullView.findViewById(R.id.nav_inner_view)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_actividades -> {
                    startActivity(Intent(this, ActividadesActivity::class.java))
                }
//                R.id.nav_cobros -> startActivity(Intent(this, PerfilActivity::class.java))
                R.id.nav_registro -> {
                    startActivity(Intent(this, RegistroSocio::class.java))
                }
//                R.id.nav_salir -> finishAffinity() // Cerrar sesión
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar?>(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)

            // Botón de "volver" en la izquierda
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
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

    fun setContentViewWithBinding(view: View) {
        val fullView = layoutInflater.inflate(R.layout.activity_base, null)
        val contentFrame = fullView.findViewById<FrameLayout>(R.id.content_frame)
        contentFrame.addView(view)
        super.setContentView(fullView)

        drawerLayout = fullView.findViewById(R.id.drawer_layout)
        navView = fullView.findViewById(R.id.nav_inner_view)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar?>(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_actividades -> startActivity(Intent(this, ActividadesActivity::class.java))
                R.id.nav_registro -> {
                    startActivity(Intent(this, RegistroSocio::class.java))
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
}