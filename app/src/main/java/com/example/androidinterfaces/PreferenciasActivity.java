package com.example.androidinterfaces;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import java.io.IOException;

/**
 * Activity para manejar preferencias del usuario
 * Demuestra:
 * - Uso de SharedPreferences para persistencia de datos
 * - Switches, SeekBar, RadioButtons
 * - Eventos onCheckedChanged, onProgressChanged
 * - Caché de configuraciones
 */
public class PreferenciasActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppPreferences";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Switch switchTemaOscuro, switchNotificaciones, switchSonidos;
    private SeekBar seekBarVolumen;
    private RadioGroup rgIdioma;
    private EditText etNombreUsuario;
    private Button btnGuardar, btnRestaurar;
    private TextView tvVolumen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);

        try {
            preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            editor = preferences.edit();
            
            initializeViews();
            cargarPreferencias();
            setupEventListeners();
        } catch (Exception e) {
            Toast.makeText(this, "Error al inicializar: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        switchTemaOscuro = findViewById(R.id.switchTemaOscuro);
        switchNotificaciones = findViewById(R.id.switchNotificaciones);
        switchSonidos = findViewById(R.id.switchSonidos);
        seekBarVolumen = findViewById(R.id.seekBarVolumen);
        rgIdioma = findViewById(R.id.rgIdioma);
        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnRestaurar = findViewById(R.id.btnRestaurar);
        tvVolumen = findViewById(R.id.tvVolumen);
    }

    /**
     * Carga las preferencias guardadas con manejo de excepciones
     */
    private void cargarPreferencias() {
        try {
            boolean temaOscuro = preferences.getBoolean("tema_oscuro", false);
            boolean notificaciones = preferences.getBoolean("notificaciones", true);
            boolean sonidos = preferences.getBoolean("sonidos", true);
            int volumen = preferences.getInt("volumen", 50);
            String idioma = preferences.getString("idioma", "espanol");
            String nombreUsuario = preferences.getString("nombre_usuario", "");

            switchTemaOscuro.setChecked(temaOscuro);
            switchNotificaciones.setChecked(notificaciones);
            switchSonidos.setChecked(sonidos);
            seekBarVolumen.setProgress(volumen);
            tvVolumen.setText("Volumen: " + volumen + "%");
            etNombreUsuario.setText(nombreUsuario);

            // Seleccionar idioma
            if (idioma.equals("espanol")) {
                rgIdioma.check(R.id.rbEspanol);
            } else if (idioma.equals("ingles")) {
                rgIdioma.check(R.id.rbIngles);
            }

            aplicarTema(temaOscuro);

        } catch (ClassCastException e) {
            Toast.makeText(this, "Error al cargar preferencias: tipo de dato incorrecto", 
                Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error general al cargar: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Configura todos los listeners de eventos
     */
    private void setupEventListeners() {
        // Evento onCheckedChanged del Switch de tema oscuro
        switchTemaOscuro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                aplicarTema(isChecked);
                Toast.makeText(PreferenciasActivity.this, 
                    "Tema " + (isChecked ? "oscuro" : "claro") + " activado", 
                    Toast.LENGTH_SHORT).show();
            }
        });

        // Evento onCheckedChanged para notificaciones
        switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, 
                "Notificaciones " + (isChecked ? "activadas" : "desactivadas"), 
                Toast.LENGTH_SHORT).show();
        });

        // Evento onProgressChanged del SeekBar
        seekBarVolumen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvVolumen.setText("Volumen: " + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Evento cuando inicia el arrastre
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(PreferenciasActivity.this, 
                    "Volumen ajustado a: " + seekBar.getProgress() + "%", 
                    Toast.LENGTH_SHORT).show();
            }
        });

        // Evento onClick del botón guardar
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarPreferencias();
            }
        });

        // Evento onClick del botón restaurar
        btnRestaurar.setOnClickListener(v -> restaurarPorDefecto());
    }

    /**
     * Guarda las preferencias con manejo de excepciones
     */
    private void guardarPreferencias() {
        try {
            editor.putBoolean("tema_oscuro", switchTemaOscuro.isChecked());
            editor.putBoolean("notificaciones", switchNotificaciones.isChecked());
            editor.putBoolean("sonidos", switchSonidos.isChecked());
            editor.putInt("volumen", seekBarVolumen.getProgress());
            
            // Guardar idioma seleccionado
            int selectedIdiomaId = rgIdioma.getCheckedRadioButtonId();
            String idioma = "espanol";
            if (selectedIdiomaId == R.id.rbIngles) {
                idioma = "ingles";
            }
            editor.putString("idioma", idioma);
            
            editor.putString("nombre_usuario", etNombreUsuario.getText().toString());

            boolean resultado = editor.commit();
            
            if (resultado) {
                Toast.makeText(this, "Preferencias guardadas exitosamente", 
                    Toast.LENGTH_SHORT).show();
            } else {
                throw new IOException("Error al guardar preferencias en disco");
            }

        } catch (IOException e) {
            Toast.makeText(this, "Error de I/O: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Restaura las preferencias por defecto
     */
    private void restaurarPorDefecto() {
        try {
            editor.clear();
            boolean resultado = editor.commit();
            
            if (resultado) {
                cargarPreferencias();
                Toast.makeText(this, "Preferencias restauradas", Toast.LENGTH_SHORT).show();
            } else {
                throw new IOException("Error al limpiar preferencias");
            }

        } catch (IOException e) {
            Toast.makeText(this, "Error de I/O: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Aplica el tema seleccionado
     */
    private void aplicarTema(boolean temaOscuro) {
        if (temaOscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}