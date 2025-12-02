package com.example.androidinterfaces;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidinterfaces.adapters.PostAdapter;
import com.example.androidinterfaces.api.ApiService;
import com.example.androidinterfaces.api.RetrofitClient;
import com.example.androidinterfaces.models.Post;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity que demuestra el consumo de API REST
 * Incluye:
 * - RecyclerView para mostrar datos
 * - Llamadas asíncronas a API
 * - Manejo completo de excepciones de red
 * - Sistema de caché de datos
 * - ProgressBar para feedback visual
 */
public class ApiActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> posts;
    private Button btnCargar, btnRecargar;
    private ProgressBar progressBar;
    private ApiService apiService;
    private boolean dataCached = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);

        try {
            initializeViews();
            setupRecyclerView();
            setupApiService();
            setupEventListeners();
        } catch (Exception e) {
            Toast.makeText(this, "Error al inicializar: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        btnCargar = findViewById(R.id.btnCargar);
        btnRecargar = findViewById(R.id.btnRecargar);
        progressBar = findViewById(R.id.progressBar);
        posts = new ArrayList<>();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(this, posts);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Inicializa el servicio de API con Retrofit
     */
    private void setupApiService() {
        try {
            apiService = RetrofitClient.getInstance().create(ApiService.class);
        } catch (Exception e) {
            Toast.makeText(this, "Error al configurar API: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    private void setupEventListeners() {
        // Evento onClick para cargar datos de la API
        btnCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataCached) {
                    Toast.makeText(ApiActivity.this, 
                        "Datos cargados desde caché", 
                        Toast.LENGTH_SHORT).show();
                } else {
                    cargarDatosApi();
                }
            }
        });

        // Evento onClick para forzar recarga desde API
        btnRecargar.setOnClickListener(v -> {
            dataCached = false;
            cargarDatosApi();
        });

        // Evento onLongClick para limpiar caché
        btnRecargar.setOnLongClickListener(v -> {
            limpiarCache();
            return true;
        });
    }

    /**
     * Carga datos desde la API con manejo completo de excepciones
     */
    private void cargarDatosApi() {
        progressBar.setVisibility(View.VISIBLE);
        btnCargar.setEnabled(false);

        try {
            Call<List<Post>> call = apiService.getPosts();
            
            call.enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    progressBar.setVisibility(View.GONE);
                    btnCargar.setEnabled(true);

                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            posts.clear();
                            posts.addAll(response.body());
                            adapter.notifyDataSetChanged();
                            dataCached = true;
                            
                            Toast.makeText(ApiActivity.this, 
                                "Cargados " + posts.size() + " posts", 
                                Toast.LENGTH_SHORT).show();
                        } else {
                            throw new IOException("Respuesta vacía o errónea del servidor");
                        }
                    } catch (IOException e) {
                        mostrarError("Error de I/O: " + e.getMessage());
                    } catch (Exception e) {
                        mostrarError("Error al procesar respuesta: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    btnCargar.setEnabled(true);

                    // Manejo específico de diferentes tipos de errores
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
            progressBar.setVisibility(View.GONE);
            btnCargar.setEnabled(true);
            mostrarError("Error al realizar llamada: " + e.getMessage());
        }
    }

    /**
     * Limpia el caché de datos
     */
    private void limpiarCache() {
        try {
            posts.clear();
            adapter.notifyDataSetChanged();
            dataCached = false;
            Toast.makeText(this, "Caché limpiado", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al limpiar caché: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Muestra mensajes de error al usuario
     */
    private void mostrarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("dataCached", dataCached);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            dataCached = savedInstanceState.getBoolean("dataCached", false);
        }
    }
}