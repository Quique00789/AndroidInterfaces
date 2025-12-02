package com.example.androidinterfaces;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import com.example.androidinterfaces.data.DatabaseHelper;
import com.example.androidinterfaces.models.Usuario;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity de búsqueda con filtrado en tiempo real
 * Demuestra:
 * - SearchView con listener
 * - Filtrado de datos en tiempo real
 * - AutoCompleteTextView
 * - Manejo de cache de resultados
 * - Eventos de texto y búsqueda
 */
public class BusquedaActivity extends AppCompatActivity {

    private SearchView searchView;
    private ListView listViewResultados;
    private AutoCompleteTextView autoCompleteTextView;
    private DatabaseHelper dbHelper;
    private List<Usuario> todosUsuarios;
    private List<Usuario> resultadosFiltrados;
    private ArrayAdapter<String> listAdapter;
    private ArrayAdapter<String> autoCompleteAdapter;
    private TextView tvResultados;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        try {
            dbHelper = new DatabaseHelper(this);
            initializeViews();
            cargarDatos();
            setupSearchView();
            setupAutoComplete();
        } catch (Exception e) {
            Toast.makeText(this, "Error al inicializar: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        searchView = findViewById(R.id.searchView);
        listViewResultados = findViewById(R.id.listViewResultados);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        tvResultados = findViewById(R.id.tvResultados);
        progressBar = findViewById(R.id.progressBar);
        
        todosUsuarios = new ArrayList<>();
        resultadosFiltrados = new ArrayList<>();
    }

    /**
     * Carga todos los usuarios desde la base de datos
     */
    private void cargarDatos() {
        progressBar.setVisibility(View.VISIBLE);
        
        try {
            todosUsuarios = dbHelper.obtenerTodosUsuarios();
            resultadosFiltrados = new ArrayList<>(todosUsuarios);
            
            actualizarLista();
            configurarAutoComplete();
            
            progressBar.setVisibility(View.GONE);

        } catch (SQLException e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error de base de datos: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error de I/O: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error general: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Configura el SearchView con su listener
     */
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscar(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Búsqueda en tiempo real
                buscar(newText);
                return true;
            }
        });

        // Evento cuando se cierra el SearchView
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                resultadosFiltrados.clear();
                resultadosFiltrados.addAll(todosUsuarios);
                actualizarLista();
                return false;
            }
        });
    }

    /**
     * Configura el AutoCompleteTextView
     */
    private void setupAutoComplete() {
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) {
                    buscarPorCiudad(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ciudad = parent.getItemAtPosition(position).toString();
                Toast.makeText(BusquedaActivity.this, 
                    "Filtrando por: " + ciudad, 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Configura el AutoComplete con lista de ciudades
     */
    private void configurarAutoComplete() {
        try {
            List<String> ciudades = new ArrayList<>();
            for (Usuario usuario : todosUsuarios) {
                if (!ciudades.contains(usuario.getCiudad())) {
                    ciudades.add(usuario.getCiudad());
                }
            }

            autoCompleteAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, ciudades);
            autoCompleteTextView.setAdapter(autoCompleteAdapter);
            autoCompleteTextView.setThreshold(1);

        } catch (Exception e) {
            Toast.makeText(this, "Error al configurar autocompletado: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Realiza la búsqueda con manejo de excepciones
     */
    private void buscar(String query) {
        try {
            resultadosFiltrados.clear();

            if (query.isEmpty()) {
                resultadosFiltrados.addAll(todosUsuarios);
            } else {
                String queryLower = query.toLowerCase();
                for (Usuario usuario : todosUsuarios) {
                    if (usuario.getNombre().toLowerCase().contains(queryLower) ||
                        usuario.getEmail().toLowerCase().contains(queryLower) ||
                        usuario.getCiudad().toLowerCase().contains(queryLower)) {
                        resultadosFiltrados.add(usuario);
                    }
                }
            }

            actualizarLista();

        } catch (NullPointerException e) {
            Toast.makeText(this, "Error: Datos nulos en búsqueda", 
                Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error en búsqueda: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Busca usuarios por ciudad
     */
    private void buscarPorCiudad(String ciudad) {
        try {
            resultadosFiltrados.clear();
            
            for (Usuario usuario : todosUsuarios) {
                if (usuario.getCiudad().toLowerCase().contains(ciudad.toLowerCase())) {
                    resultadosFiltrados.add(usuario);
                }
            }

            actualizarLista();

        } catch (Exception e) {
            Toast.makeText(this, "Error al buscar por ciudad: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Actualiza la lista de resultados
     */
    private void actualizarLista() {
        try {
            List<String> nombres = new ArrayList<>();
            for (Usuario usuario : resultadosFiltrados) {
                nombres.add(usuario.getNombre() + " - " + usuario.getCiudad());
            }

            listAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, nombres);
            listViewResultados.setAdapter(listAdapter);

            tvResultados.setText("Resultados encontrados: " + resultadosFiltrados.size());

        } catch (Exception e) {
            Toast.makeText(this, "Error al actualizar lista: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}