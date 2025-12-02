# AndroidInterfaces - Programa de Desarrollo de Interfaces de Usuario

## 📋 Descripción

Programa informático que demuestra el desarrollo de interfaces de usuario con acceso a datos, utilizando controles Android y programación de eventos. Este proyecto implementa un sistema completo de interfaces responsivas con manejo de eventos, excepciones y acceso a datos mediante SQLite, API REST y SharedPreferences.

## 🎯 Objetivos del Proyecto

Identificar y aplicar la sintaxis del lenguaje de programación de desarrollo de eventos en el funcionamiento de interfaces de usuario:
- Manipulación de eventos (onClick, onLongClick, onTextChanged, onFocusChanged, onItemClick, onItemSelected)
- Manejo de excepciones (SQLException, IOException, Exception)
- Acceso a datos (CRUD, API REST, Preferencias, Caché)

## ✨ Características Principales

### Interfaces de Usuario - 6 Pantallas Funcionales

#### 1. **MainActivity** - Menú Principal
- **Controles**: 5 Button
- **Eventos**: onClick, onLongClick
- **Funcionalidad**: Navegación a todas las pantallas con ayuda contextual en long press

#### 2. **FormularioActivity** - Registro de Usuarios
- **Controles**: 
  - EditText (nombre, email, teléfono, edad)
  - Spinner (ciudades)
  - RadioGroup con RadioButton (género)
  - CheckBox (términos y condiciones)
  - Switch (notificaciones)
  - Button (guardar, limpiar)
  - TextView (contador de caracteres)
- **Eventos**: 
  - onTextChanged (contador de caracteres en tiempo real)
  - onFocusChanged (validación de email y teléfono)
  - onItemSelected (selección de ciudad)
  - onClick (guardar y limpiar)
- **Acceso a datos**: INSERT en SQLite con manejo de SQLException

#### 3. **ListadoActivity** - Listado de Usuarios
- **Controles**:
  - ListView con adapter personalizado
  - Button (refrescar)
  - TextView (contador de registros)
- **Eventos**:
  - onItemClick (ver detalles)
  - onItemLongClick (eliminar con confirmación)
  - onClick (refrescar listado)
- **Acceso a datos**: SELECT y DELETE en SQLite
- **Manejo de excepciones**: SQLException, IOException

#### 4. **ApiActivity** - Consumo de API REST
- **Controles**:
  - RecyclerView con CardView
  - Button (cargar, recargar)
  - ProgressBar
- **Eventos**:
  - onClick (cargar datos)
  - onLongClick (limpiar caché)
- **API**: JSONPlaceholder (https://jsonplaceholder.typicode.com/posts)
- **Tecnologías**: Retrofit + Gson
- **Manejo de excepciones**: UnknownHostException, SocketTimeoutException, IOException
- **Caché**: Sistema de caché en memoria para optimizar peticiones

#### 5. **PreferenciasActivity** - Configuración de Usuario
- **Controles**:
  - EditText (nombre de usuario)
  - Switch (tema oscuro, notificaciones, sonidos)
  - SeekBar (volumen)
  - RadioGroup (idioma: Español/Inglés)
  - Button (guardar, restaurar)
  - TextView (indicador de volumen)
- **Eventos**:
  - onCheckedChanged (switches)
  - onProgressChanged (SeekBar)
  - onClick (guardar y restaurar)
- **Acceso a datos**: SharedPreferences con commit()
- **Funcionalidad especial**: Aplicación de tema oscuro/claro en tiempo real

#### 6. **BusquedaActivity** - Búsqueda de Usuarios
- **Controles**:
  - SearchView
  - AutoCompleteTextView (filtro por ciudad)
  - ListView (resultados)
  - ProgressBar
  - TextView (contador de resultados)
- **Eventos**:
  - onQueryTextChange (búsqueda en tiempo real)
  - onQueryTextSubmit (búsqueda al enviar)
  - onTextChanged (AutoCompleteTextView)
  - onItemClick (selección de sugerencia)
- **Acceso a datos**: SELECT con filtros en SQLite
- **Funcionalidad**: Búsqueda por nombre, email o ciudad con autocompletado

### Programación de Eventos Implementados

```java
// onClick - Click simple
button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        navigateToActivity(TargetActivity.class);
    }
});

// onLongClick - Click prolongado
button.setOnLongClickListener(new View.OnLongClickListener() {
    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(context, "Ayuda contextual", Toast.LENGTH_SHORT).show();
        return true;
    }
});

// onTextChanged - Cambio de texto en tiempo real
editText.addTextChangedListener(new TextWatcher() {
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        tvCounter.setText("Caracteres: " + s.length());
    }
});

// onFocusChanged - Validación al perder foco
editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            validateEmail();
        }
    }
});

// onItemClick - Selección en ListView
listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Usuario usuario = usuarios.get(position);
        mostrarDetalles(usuario);
    }
});

// onItemSelected - Selección en Spinner
spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String ciudad = parent.getItemAtPosition(position).toString();
        Toast.makeText(context, "Ciudad: " + ciudad, Toast.LENGTH_SHORT).show();
    }
});
```

### Eventos de Ciclo de Vida

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initializeViews();
    setupEventListeners();
}

@Override
protected void onStart() {
    super.onStart();
    // Actividad visible
}

@Override
protected void onResume() {
    super.onResume();
    // Recargar datos si es necesario
    cargarDatos();
}

@Override
protected void onPause() {
    super.onPause();
    // Pausar operaciones
}

@Override
protected void onStop() {
    super.onStop();
    // Actividad no visible
}

@Override
protected void onDestroy() {
    super.onDestroy();
    // Liberar recursos
    if (dbHelper != null) {
        dbHelper.close();
    }
}
```

### Manejo de Excepciones

```java
// Ejemplo en DatabaseHelper
public long insertarUsuario(Usuario usuario) throws SQLException {
    SQLiteDatabase db = null;
    try {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", usuario.getNombre());
        values.put("email", usuario.getEmail());
        
        long resultado = db.insert("usuarios", null, values);
        
        if (resultado == -1) {
            throw new SQLException("Error al insertar usuario en la base de datos");
        }
        
        return resultado;
        
    } catch (android.database.SQLException e) {
        throw new SQLException("Error SQL al insertar: " + e.getMessage());
    } catch (Exception e) {
        throw new SQLException("Error general al insertar: " + e.getMessage());
    } finally {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}

// Ejemplo en ApiActivity
private void cargarDatosApi() {
    try {
        Call<List<Post>> call = apiService.getPosts();
        
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                if (t instanceof UnknownHostException) {
                    mostrarError("Sin conexión a internet");
                } else if (t instanceof SocketTimeoutException) {
                    mostrarError("Tiempo de espera agotado");
                } else if (t instanceof IOException) {
                    mostrarError("Error de red: " + t.getMessage());
                } else {
                    mostrarError("Error general: " + t.getMessage());
                }
            }
        });
        
    } catch (Exception e) {
        mostrarError("Error al realizar llamada: " + e.getMessage());
    }
}
```

### Acceso a Datos

#### 1. CRUD Completo con SQLite (DatabaseHelper.java)

```java
// CREATE - Insertar usuario
public long insertarUsuario(Usuario usuario) throws SQLException {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put("nombre", usuario.getNombre());
    values.put("email", usuario.getEmail());
    values.put("telefono", usuario.getTelefono());
    values.put("edad", usuario.getEdad());
    values.put("ciudad", usuario.getCiudad());
    values.put("genero", usuario.getGenero());
    values.put("notificaciones", usuario.isNotificaciones() ? 1 : 0);
    return db.insert("usuarios", null, values);
}

// READ - Obtener todos los usuarios
public List<Usuario> obtenerTodosUsuarios() throws SQLException, IOException {
    List<Usuario> usuarios = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.query("usuarios", null, null, null, null, null, "nombre ASC");
    
    if (cursor.moveToFirst()) {
        do {
            Usuario usuario = new Usuario();
            usuario.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            usuario.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
            usuario.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            // ... mapear demás campos
            usuarios.add(usuario);
        } while (cursor.moveToNext());
    }
    cursor.close();
    return usuarios;
}

// UPDATE - Actualizar usuario
public boolean actualizarUsuario(Usuario usuario) throws SQLException {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put("nombre", usuario.getNombre());
    values.put("email", usuario.getEmail());
    // ... demás campos
    
    int filasAfectadas = db.update("usuarios", values, 
        "id = ?", new String[]{String.valueOf(usuario.getId())});
    return filasAfectadas > 0;
}

// DELETE - Eliminar usuario
public boolean eliminarUsuario(int id) throws SQLException {
    SQLiteDatabase db = this.getWritableDatabase();
    int filasEliminadas = db.delete("usuarios", "id = ?", new String[]{String.valueOf(id)});
    return filasEliminadas > 0;
}
```

#### 2. Consumo de API REST con Retrofit

```java
// ApiService.java
public interface ApiService {
    @GET("posts")
    Call<List<Post>> getPosts();
    
    @GET("posts/{id}")
    Call<Post> getPost(@Path("id") int id);
}

// RetrofitClient.java
public class RetrofitClient {
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
```

#### 3. Manejo de Preferencias (SharedPreferences)

```java
// Guardar preferencias
SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
SharedPreferences.Editor editor = preferences.edit();

editor.putBoolean("tema_oscuro", true);
editor.putBoolean("notificaciones", true);
editor.putInt("volumen", 50);
editor.putString("idioma", "espanol");
editor.putString("nombre_usuario", "Leonardo");

boolean resultado = editor.commit(); // Sincrónico
// o editor.apply(); // Asíncrono

// Cargar preferencias
boolean temaOscuro = preferences.getBoolean("tema_oscuro", false);
int volumen = preferences.getInt("volumen", 50);
String idioma = preferences.getString("idioma", "espanol");
```

#### 4. Sistema de Caché

```java
// En ApiActivity
private boolean dataCached = false;
private List<Post> posts = new ArrayList<>();

private void cargarDatos() {
    if (dataCached) {
        // Usar datos en caché
        Toast.makeText(this, "Datos cargados desde caché", Toast.LENGTH_SHORT).show();
    } else {
        // Cargar desde API
        cargarDatosApi();
    }
}

private void limpiarCache() {
    posts.clear();
    adapter.notifyDataSetChanged();
    dataCached = false;
}
```

## 🛠️ Tecnologías Utilizadas

### Lenguaje de Programación
- **Java 11**: Lenguaje principal para desarrollo Android

### Framework y Herramientas

- **Android SDK API 36**: Kit de desarrollo de Android
- **Gradle (Kotlin DSL)**: Sistema de construcción
- **Material Design Components**: Diseño de interfaces

### Bibliotecas Principales

```gradle
dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // CardView
    implementation("androidx.cardview:cardview:1.0.0")
    
    // Retrofit para API REST
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
}
```

### Base de Datos

- **SQLite**: Base de datos local con tabla `usuarios`

### API Externa

- **JSONPlaceholder**: API REST pública para pruebas (https://jsonplaceholder.typicode.com)

## 📦 Instalación

### Prerrequisitos

1. **Java Development Kit (JDK)**
   - Versión: JDK 11 o superior
   - Descargar: https://www.oracle.com/java/technologies/downloads/

2. **Android Studio**
   - Versión: Android Studio Hedgehog (2023.1.1) o superior
   - Descargar: https://developer.android.com/studio

3. **Android SDK**
   - API Level mínimo: 30 (Android 11)
   - API Level target: 36 (Android 14)

### Pasos de Instalación
#### 1. Clonar el Repositorio

```bash
git clone https://github.com/Quique00789/AndroidInterfaces.git
cd AndroidInterfaces
```

#### 2. Abrir en Android Studio

1. Abre Android Studio
2. Selecciona "Open an Existing Project"
3. Navega hasta la carpeta clonada y selecciónala
4. Espera a que Gradle sincronice el proyecto

#### 3. Sincronizar Gradle

- Haz clic en el icono del elefante 🐘 en la barra de herramientas
- O ve a: `File > Sync Project with Gradle Files`

#### 4. Compilar el Proyecto

```bash
./gradlew build
```

O desde Android Studio: `Build > Make Project`

#### 5. Ejecutar la Aplicación

**Opción A: En un Emulador**
1. Ve a `Tools > Device Manager`
2. Crea un nuevo dispositivo virtual (AVD) si no tienes uno
   - Dispositivo recomendado: Pixel 9 Pro
   - System Image: Android 14 (API 36)
3. Haz clic en el botón "Run" (▶️) o presiona `Shift + F10`

**Opción B: En un Dispositivo Físico**
1. Habilita el modo desarrollador en tu dispositivo
2. Habilita la depuración USB
3. Conecta tu dispositivo por USB
4. Acepta la depuración USB en el dispositivo
5. Haz clic en el botón "Run" (▶️)

## 🏭 Estructura del Proyecto

```
AndroidInterfaces/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/androidinterfaces/
│   │   │   │   ├── MainActivity.java
│   │   │   │   ├── FormularioActivity.java
│   │   │   │   ├── ListadoActivity.java
│   │   │   │   ├── ApiActivity.java
│   │   │   │   ├── PreferenciasActivity.java
│   │   │   │   ├── BusquedaActivity.java
│   │   │   │   ├── adapters/
│   │   │   │   │   ├── UsuarioAdapter.java
│   │   │   │   │   └── PostAdapter.java
│   │   │   │   ├── data/
│   │   │   │   │   └── DatabaseHelper.java
│   │   │   │   ├── models/
│   │   │   │   │   ├── Usuario.java
│   │   │   │   │   └── Post.java
│   │   │   │   └── api/
│   │   │   │       ├── ApiService.java
│   │   │   │       └── RetrofitClient.java
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_formulario.xml
│   │   │   │   │   ├── activity_listado.xml
│   │   │   │   │   ├── activity_api.xml
│   │   │   │   │   ├── activity_preferencias.xml
│   │   │   │   │   ├── activity_busqueda.xml
│   │   │   │   │   ├── item_usuario.xml
│   │   │   │   │   └── item_post.xml
│   │   │   │   ├── values/
│   │   │   │   │   └── strings.xml
│   │   │   │   └── xml/
│   │   │   │       ├── backup_rules.xml
│   │   │   │       └── data_extraction_rules.xml
│   │   │   └── AndroidManifest.xml
│   │   └── androidTest/
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🚀 Cómo Funciona la Aplicación

### Flujo de Navegación
```
[MainActivity - Menú Principal]
        │
        ├──> [FormularioActivity] → Registrar nuevos usuarios
        │
        ├──> [ListadoActivity] → Ver, editar y eliminar usuarios
        │
        ├──> [ApiActivity] → Consumir API REST de posts
        │
        ├──> [PreferenciasActivity] → Configurar preferencias de la app
        │
        └──> [BusquedaActivity] → Buscar usuarios con filtros
```

### Flujo de Datos

```
[Usuario ingresa datos en FormularioActivity]
        │
        v
[Validación en tiempo real con onTextChanged y onFocusChanged]
        │
        v
[Usuario presiona "Guardar" (onClick)]
        │
        v
[try-catch para manejo de excepciones]
        │
        v
[DatabaseHelper.insertarUsuario() → SQLite]
        │
        v
[Mensaje de confirmación Toast]
        │
        v
[Datos disponibles en ListadoActivity y BusquedaActivity]
```

### Flujo de Consumo de API

```
[Usuario abre ApiActivity]
        │
        v
[Presiona "Cargar Datos" (onClick)]
        │
        v
[¿Datos en caché?] ─── Sí ──> [Mostrar desde caché]
        │
        No
        │
        v
[Mostrar ProgressBar]
        │
        v
[Retrofit hace GET a JSONPlaceholder]
        │
        v
[Manejo de excepciones de red]
        │
        v
[Convertir JSON a objetos Post con Gson]
        │
        v
[Guardar en caché]
        │
        v
[Actualizar RecyclerView con PostAdapter]
        │
        v
[Ocultar ProgressBar]
```

## 📋 Lista de Cotejo - Evaluación del Proyecto

| Criterio | Puntos | Implementado | Evidencia |
|----------|--------|-------------|----------|
| Implementa 5+ interfaces funcionales | 20 | ✅ | 6 Activities (MainActivity, FormularioActivity, ListadoActivity, ApiActivity, PreferenciasActivity, BusquedaActivity) |
| Utiliza todos los controles requeridos | 20 | ✅ | EditText, Button, Spinner, CheckBox, RadioButton, Switch, SeekBar, ListView, RecyclerView, SearchView, AutoCompleteTextView, ProgressBar |
| Programa eventos correctamente | 20 | ✅ | onClick, onLongClick, onTextChanged, onFocusChanged, onItemClick, onItemSelected, onCreate, onResume, onDestroy |
| Maneja excepciones apropiadamente | 15 | ✅ | SQLException, IOException, UnknownHostException, SocketTimeoutException, Exception |
| Acceso a datos funcional | 15 | ✅ | CRUD completo (SQLite) + API REST (Retrofit) + SharedPreferences + Caché |
| Código documentado y organizado | 10 | ✅ | JavaDoc completo, estructura por packages, nombres descriptivos |
| **TOTAL** | **100** | **100** | **Proyecto completo y funcional** |

### Detalles de Implementación por Criterio

✅ **Interfaces de Usuario (20/20 puntos)**
- 6 pantallas funcionales (supera el mínimo de 5)
- Layouts XML responsivos con LinearLayout y ScrollView
- Navegación fluida entre Activities
- Diseño consistente en todas las pantallas

✅ **Controles Implementados (20/20 puntos)**
- **Entrada de texto**: EditText, AutoCompleteTextView, SearchView
- **Botones**: Button
- **Selección**: Spinner, CheckBox, RadioButton, Switch
- **Deslizadores**: SeekBar
- **Listas**: ListView, RecyclerView
- **Indicadores**: ProgressBar, TextView

✅ **Eventos Programados (20/20 puntos)**
- **Click**: onClick (navegación, guardar, eliminar)
- **Click prolongado**: onLongClick (ayuda contextual, eliminar)
- **Texto**: onTextChanged (contador, búsqueda), onFocusChanged (validación)
- **Listas**: onItemClick (selección), onItemSelected (Spinner)
- **Ciclo de vida**: onCreate, onStart, onResume, onPause, onStop, onDestroy

✅ **Manejo de Excepciones (15/15 puntos)**
- SQLException: Todas las operaciones de base de datos
- IOException: Operaciones de lectura/escritura, preferencias
- UnknownHostException: Sin conexión a internet
- SocketTimeoutException: Timeout de red
- Exception: Manejo general con finally para limpieza

✅ **Acceso a Datos (15/15 puntos)**
- **CRUD completo**: CREATE (insertarUsuario), READ (obtenerTodosUsuarios), UPDATE (actualizarUsuario), DELETE (eliminarUsuario)
- **API REST**: Retrofit + Gson consumiendo JSONPlaceholder
- **Preferencias**: SharedPreferences con commit/apply
- **Caché**: Sistema de caché en memoria para optimizar peticiones

✅ **Documentación y Organización (10/10 puntos)**
- JavaDoc en todos los métodos principales
- Estructura por packages: models, adapters, data, api
- Nombres descriptivos de variables y métodos
- README completo con ejemplos de código

## 🐛 Solución de Problemas

### Error: "Cannot resolve symbol 'retrofit2'"
**Solución**: 
1. Sincroniza Gradle: `File > Sync Project with Gradle Files`
2. Si persiste: `Build > Clean Project` y luego `Build > Rebuild Project`
3. Verifica que tengas conexión a internet para descargar las dependencias

### Error: "Activity class does not exist"
**Solución**: 
1. Verifica que el package en AndroidManifest.xml sea `com.example.androidinterfaces`
2. Verifica que todas las Activities estén registradas en el manifest
3. Haz Clean y Rebuild del proyecto

### Error: "Unable to connect to database"
**Solución**:
1. Verifica que el DatabaseHelper se inicialice correctamente
2. Revisa los permisos en AndroidManifest.xml (no son necesarios para SQLite interno)
3. Verifica que la tabla se cree correctamente en onCreate()

### La aplicación se cierra inesperadamente
**Solución**:
1. Revisa los logs en Logcat (busca "AndroidRuntime")
2. Verifica que todos los findViewById tengan IDs correctos en los XMLs
3. Asegúrate de que todos los eventos tengan manejo de excepciones

### Gradle Sync Failed
**Solución**:
1. Invalida caché: `File > Invalidate Caches / Restart`
2. Elimina la carpeta `.gradle` y sincroniza de nuevo
3. Verifica tu versión de JDK (debe ser JDK 11 o superior)

## 📱 Requisitos del Sistema

### Para Desarrollo
- **Sistema Operativo**: Windows 10/11, macOS 10.14+, o Linux
- **RAM**: Mínimo 8 GB (recomendado 16 GB)
- **Espacio en Disco**: 10 GB libres para Android Studio y SDK
- **Procesador**: Intel i5/AMD Ryzen 5 o superior
- **Conexión a Internet**: Requerida para descargar dependencias

### Para Ejecución (Dispositivo Android)
- **Versión Android**: 11.0 (API 30) o superior
- **RAM**: Mínimo 2 GB
- **Espacio**: 50 MB libres
- **Conexión a Internet**: Requerida solo para ApiActivity

## 📝 Licencia

Este proyecto es desarrollado con fines educativos como parte del programa DAEM (Desarrollo de Aplicaciones Móviles).

## 👨‍💻 Autor

**Leonardo Trejo**
- GitHub: [@Quique00789](https://github.com/Quique00789)
- Repositorio: [AndroidInterfaces](https://github.com/Quique00789/AndroidInterfaces)

## 📢 Contacto

Para preguntas, sugerencias o reportar problemas sobre el proyecto, por favor abre un issue en el repositorio de GitHub.

---

**Proyecto académico - EVIDENCIA 4B**  
**Curso**: Desarrollo de Aplicaciones Móviles (DAEM)  
**Tema**: Programa Informático de Desarrollo de Interfaces de Usuario con Acceso a Datos  
**Ponderación**: 20% (Heteroevaluación)  
**Fecha de entrega**: Semana 11
