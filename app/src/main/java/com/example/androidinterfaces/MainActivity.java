package com.example.androidinterfaces;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity principal que actúa como menú de navegación
 * Demuestra el uso de eventos onClick y navegación entre pantallas
 */
public class MainActivity extends AppCompatActivity {

    private Button btnFormulario, btnListado, btnApi, btnPreferencias, btnBusqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Toast.makeText(this, "Aplicación iniciada", Toast.LENGTH_SHORT).show();
        
        initializeViews();
        setupEventListeners();
    }

    /**
     * Inicializa las vistas de la actividad
     */
    private void initializeViews() {
        try {
            btnFormulario = findViewById(R.id.btnFormulario);
            btnListado = findViewById(R.id.btnListado);
            btnApi = findViewById(R.id.btnApi);
            btnPreferencias = findViewById(R.id.btnPreferencias);
            btnBusqueda = findViewById(R.id.btnBusqueda);
        } catch (Exception e) {
            Toast.makeText(this, "Error al inicializar vistas: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Configura los listeners de eventos para los botones
     * Demuestra el uso de eventos onClick y onLongClick
     */
    private void setupEventListeners() {
        // Evento onClick para navegar al formulario
        btnFormulario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToActivity(FormularioActivity.class);
            }
        });

        // Evento onLongClick para mostrar ayuda
        btnFormulario.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "Formulario: Crear nuevos registros", 
                    Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        btnListado.setOnClickListener(v -> navigateToActivity(ListadoActivity.class));
        btnListado.setOnLongClickListener(v -> {
            Toast.makeText(this, "Listado: Ver todos los registros", Toast.LENGTH_SHORT).show();
            return true;
        });

        btnApi.setOnClickListener(v -> navigateToActivity(ApiActivity.class));
        btnApi.setOnLongClickListener(v -> {
            Toast.makeText(this, "API: Consumir servicios REST", Toast.LENGTH_SHORT).show();
            return true;
        });

        btnPreferencias.setOnClickListener(v -> navigateToActivity(PreferenciasActivity.class));
        btnBusqueda.setOnClickListener(v -> navigateToActivity(BusquedaActivity.class));
    }

    /**
     * Navega a una actividad específica con manejo de excepciones
     */
    private void navigateToActivity(Class<?> activityClass) {
        try {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error al navegar: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Evento de ciclo de vida
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Evento de ciclo de vida
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Evento de ciclo de vida
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Evento de ciclo de vida
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpieza de recursos
    }
}