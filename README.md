# 📱 Proyecto Integrador DAM

Aplicación Android desarrollada en Kotlin como parte del Proyecto Integrador de Desarrollo de Aplicaciones Móviles (DAM). Esta app permite gestionar actividades, registrar socios y administrar cobros, todo desde una interfaz moderna con menú lateral (DrawerLayout) y diseño modular.

---

## 🧾 Descripción general

Esta aplicación está pensada para la gestión interna de un club o institución, y ofrece las siguientes funcionalidades clave:

- **Registro de socios**: Formulario simple y efectivo.
- **Gestión de actividades**: Visualización y creación de nuevas actividades con cupos.
- **Administración de cobros**: Comprobante de pago y sección de pagos.
- **Navegación lateral personalizada**: Menú tipo modal desde la derecha, con header, footer y botones personalizados.

---

## 🛠️ Tecnologías y herramientas utilizadas

- **Lenguaje**: Kotlin
- **IDE**: Android Studio (Gradle + ViewBinding)
- **Diseño UI**:
  - DrawerLayout
  - ConstraintLayout
  - Material Design
- **Estructura modular**:
  - `BaseActivity.kt`: clase base para unificar navegación y Toolbar
  - Layouts reusables (`nav_header`, `nav_footer`, `nav_menu_items`)
- **Versión mínima de SDK**: API 24 (Android 7.0)

---

## 🧩 Estructura modular y extensión

El proyecto está preparado para escalar fácilmente. Cada `Activity` nueva que requiera Toolbar + Menú lateral sólo debe seguir este patrón:

### ➕ Cómo crear una nueva pantalla con menú lateral y botón de volver atrás (toolbar)

1. **Extender `BaseActivity` en lugar de `AppCompatActivity`:**

    ```	kotlin
     class NuevaPantallaActivity : BaseActivity() {
         private lateinit var binding: ActivityNuevaPantallaBinding
         override fun onCreate(savedInstanceState: Bundle?) {
         	super.onCreate(savedInstanceState)
         	binding = ActivityNuevaPantallaBinding.inflate(layoutInflater)
         	setContentViewWithBinding(binding.root)
     	}
 	}
   ```

2. **Incluir el Toolbar en el layout XML:**

 ``` xml
  <androidx.appcompat.widget.Toolbar
  		android:id="@+id/toolbar"
  		android:layout_width="match_parent"
  		android:layout_height="?attr/actionBarSize"
  		android:background="@android:color/transparent"
  		android:elevation="0dp"
  		app:title=""
  		app:titleTextColor="@android:color/black"
  		app:popupTheme="@style/ThemeOverlay.AppCompat.Light" />
   ```

	🔹 Este Toolbar es detectado y configurado automáticamente por BaseActivity.
	
## 🧑‍💻 Uso de la aplicación

* Al iniciar, se deberá iniciar sesión o registrarse en el caso de no contar con usuario y contraseña.
	* En el caso de necesitar registro, se deberá completar un formulario en las siguientes 2 páginas con datos personales y con el usuario y contraseña que deseemos tener.

* Una vez iniciada sesión se accede al menú principal el cual contiene funciones básicas para el manejo del club.

* Desde el ícono hamburguesa (arriba a la derecha), se accede al menú lateral:

	* Cobros
	* Actividades
	* Registro
	* Cerrar sesión

**El botón del menú correspondiente a la pantalla actual se resalta y desactiva automáticamente para evitar recargas innecesarias.**

## 🗂️ Estructura de carpetas (resumida)

```psql
ProyectoIntegradorDAM/
├── java/com/example/proyectointegradordam/
│   ├── BaseActivity.kt                  # Base para todas las pantallas con menú y toolbar
│   ├── MainActivity.kt                  # Pantalla inicial
│   ├── RegistroSocio.kt                 # Registro de socios
│   ├── PaymentActivity.kt               # Sección de cobros
│   ├── view/
│   │   └── ActividadesActivity.kt     # Gestión de actividades
│
├── res/
│   ├── layout/
│   │   ├── activity_base.xml               # Contenedor principal con DrawerLayout
│   │   ├── nav_header.xml                  # Header del menú lateral
│   │   ├── nav_footer.xml                  # Botón de cerrar sesión
│   │   ├── nav_menu_items.xml              # Botones de navegación
│   │   ├── modal_form_payment_receipt.xml  # Modal de comprobante de pago
│   │   ├── activity_payment.xml            # Pantalla de pagos
│   │   ├── activity_registro_socio.xml     # Pantalla de registro
│   │   ├── activity_actividades.xml        # Pantalla de actividades
│   │
│   ├── menu/
│   │   └── toolbar_menu.xml                # Ícono del menú hamburguesa
│   │
│   ├── drawable/
│   │   ├── fondo_difuminado.png
│   │   └── ic_*.xml
│   │
│   └── values/
│       ├── strings.xml
│       ├── colors.xml
│       └── themes.xml
├── build.gradle.kts
└── settings.gradle.kts
```
## 🚀 Cómo ejecutar la aplicación
1. Clonar el repositorio:

  ```bash
git clone https://github.com/illeiva2/ProyectoIntegradorDAM.git
  ```

2. Abrir en Android Studio:

 * Seleccioná “Open an existing project” y buscá la carpeta clonada.

3. Ejecutar en un emulador o dispositivo:

 * Hacé clic en el botón de “Run” (▶️)

## 👤 Autores

**Ignacio Toledo Roll**

**Gonzalo Paz**

**Romina Gargano**

**Laura Vivan**

**Iván Leiva**

## 📝 Licencia
Este proyecto está licenciado bajo la MIT License.
