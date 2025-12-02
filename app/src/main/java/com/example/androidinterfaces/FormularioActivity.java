package com.example.androidinterfaces;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidinterfaces.data.DatabaseHelper;
import com.example.androidinterfaces.models.Usuario;
import java.sql.SQLException;

/**
 * Activity de formulario que demuestra:
 * - Uso de múltiples controles (EditText, Spinner, CheckBox, RadioButton, Switch)
 * - Eventos onTextChanged, onFocusChanged, onItemSelected
 * - Validación de datos y manejo de excepciones
 * - Operaciones CRUD en base de datos
 */
public class FormularioActivity extends AppCompatActivity {

    private EditText etNombre, etEmail, etTelefono, etEdad;
    private Spinner spinnerCiudad;
    private CheckBox cbAceptaTerminos;
    private RadioGroup rgGenero;
    private Switch switchNotificaciones;
    private Button btnGuardar, btnLimpiar;
    private DatabaseHelper dbHelper;
    private TextView tvCaracteresNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        try {
            dbHelper = new DatabaseHelper(this);
            initializeViews();
            setupSpinner();
            setupEventListeners();
        } catch (Exception e) {
            Toast.makeText(this, "Error al inicializar: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etTelefono = findViewById(R.id.etTelefono);
        etEdad = findViewById(R.id.etEdad);
        spinnerCiudad = findViewById(R.id.spinnerCiudad);
        cbAceptaTerminos = findViewById(R.id.cbAceptaTerminos);
        rgGenero = findViewById(R.id.rgGenero);
        switchNotificaciones = findViewById(R.id.switchNotificaciones);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        tvCaracteresNombre = findViewById(R.id.tvCaracteresNombre);
    }

    /**
     * Configura el Spinner con lista de ciudades
     */
    private void setupSpinner() {
        String[] ciudades = {"Selecciona ciudad", "Ciudad de México", "Guadalajara", 
            "Monterrey", "Puebla", "Tijuana", "León"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, ciudades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCiudad.setAdapter(adapter);
    }

    /**
     * Configura todos los listeners de eventos
     */
    private void setupEventListeners() {
        // Evento onTextChanged - Contador de caracteres
        etNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCaracteresNombre.setText("Caracteres: " + s.length());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Evento onFocusChanged - Validación en tiempo real
        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String email = etEmail.getText().toString();
                    if (!email.contains("@")) {
                        etEmail.setError("Email inválido");
                    }
                }
            }
        });

        etTelefono.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String telefono = etTelefono.getText().toString();
                if (telefono.length() != 10) {
                    etTelefono.setError("El teléfono debe tener 10 dígitos");
                }
            }
        });

        // Evento onItemSelected del Spinner
        spinnerCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String ciudad = parent.getItemAtPosition(position).toString();
                    Toast.makeText(FormularioActivity.this, "Ciudad: " + ciudad, 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Evento onClick del botón guardar
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarUsuario();
            }
        });

        // Evento onClick del botón limpiar
        btnLimpiar.setOnClickListener(v -> limpiarFormulario());
    }

    /**
     * Guarda el usuario en la base de datos con manejo completo de excepciones
     */
    private void guardarUsuario() {
        try {
            // Validación de campos requeridos
            if (!validarCampos()) {
                return;
            }

            // Obtener datos del formulario
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            int edad = Integer.parseInt(etEdad.getText().toString().trim());
            String ciudad = spinnerCiudad.getSelectedItem().toString();
            
            // Obtener género seleccionado
            int selectedGeneroId = rgGenero.getCheckedRadioButtonId();
            RadioButton rbGenero = findViewById(selectedGeneroId);
            String genero = rbGenero != null ? rbGenero.getText().toString() : "No especificado";
            
            boolean notificaciones = switchNotificaciones.isChecked();

            // Crear objeto Usuario
            Usuario usuario = new Usuario(0, nombre, email, telefono, edad, 
                ciudad, genero, notificaciones);

            // Guardar en base de datos
            long resultado = dbHelper.insertarUsuario(usuario);
            
            if (resultado != -1) {
                Toast.makeText(this, "Usuario guardado exitosamente", 
                    Toast.LENGTH_LONG).show();
                limpiarFormulario();
            } else {
                throw new SQLException("Error al insertar en la base de datos");
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error: La edad debe ser un número válido", 
                Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Toast.makeText(this, "Error de base de datos: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error general: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Valida que todos los campos requeridos estén completos
     */
    private boolean validarCampos() {
        if (etNombre.getText().toString().trim().isEmpty()) {
            etNombre.setError("Campo requerido");
            return false;
        }
        if (etEmail.getText().toString().trim().isEmpty()) {
            etEmail.setError("Campo requerido");
            return false;
        }
        if (etTelefono.getText().toString().trim().isEmpty()) {
            etTelefono.setError("Campo requerido");
            return false;
        }
        if (etEdad.getText().toString().trim().isEmpty()) {
            etEdad.setError("Campo requerido");
            return false;
        }
        if (spinnerCiudad.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecciona una ciudad", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!cbAceptaTerminos.isChecked()) {
            Toast.makeText(this, "Debes aceptar los términos y condiciones", 
                Toast.LENGTH_SHORT).show();
            return false;
        }
        if (rgGenero.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Selecciona un género", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Limpia todos los campos del formulario
     */
    private void limpiarFormulario() {
        etNombre.setText("");
        etEmail.setText("");
        etTelefono.setText("");
        etEdad.setText("");
        spinnerCiudad.setSelection(0);
        cbAceptaTerminos.setChecked(false);
        rgGenero.clearCheck();
        switchNotificaciones.setChecked(false);
        tvCaracteresNombre.setText("Caracteres: 0");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}