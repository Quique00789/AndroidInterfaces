package com.example.androidinterfaces;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidinterfaces.adapters.UsuarioAdapter;
import com.example.androidinterfaces.data.DatabaseHelper;
import com.example.androidinterfaces.models.Usuario;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity que muestra un listado de usuarios
 * Demuestra:
 * - Uso de ListView con adapter personalizado
 * - Eventos onItemClick y onItemLongClick
 * - Operaciones CRUD (Read, Update, Delete)
 * - Manejo de excepciones de base de datos e I/O
 */
public class ListadoActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseHelper dbHelper;
    private List<Usuario> usuarios;
    private UsuarioAdapter adapter;
    private Button btnRefrescar;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        try {
            dbHelper = new DatabaseHelper(this);
            initializeViews();
            cargarDatos();
            setupEventListeners();
        } catch (Exception e) {
            Toast.makeText(this, "Error al inicializar: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        listView = findViewById(R.id.listView);
        btnRefrescar = findViewById(R.id.btnRefrescar);
        tvTotal = findViewById(R.id.tvTotal);
    }

    /**
     * Carga los datos desde la base de datos con manejo de excepciones
     */
    private void cargarDatos() {
        try {
            usuarios = dbHelper.obtenerTodosUsuarios();
            
            if (usuarios == null) {
                usuarios = new ArrayList<>();
                throw new SQLException("Error al obtener usuarios de la base de datos");
            }

            adapter = new UsuarioAdapter(this, usuarios);
            listView.setAdapter(adapter);
            
            tvTotal.setText("Total de registros: " + usuarios.size());

            if (usuarios.isEmpty()) {
                Toast.makeText(this, "No hay registros para mostrar", 
                    Toast.LENGTH_SHORT).show();
            }

        } catch (SQLException e) {
            Toast.makeText(this, "Error de base de datos: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
            usuarios = new ArrayList<>();
            adapter = new UsuarioAdapter(this, usuarios);
            listView.setAdapter(adapter);
        } catch (IOException e) {
            Toast.makeText(this, "Error de I/O al cargar datos: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error general: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Configura los listeners de eventos
     */
    private void setupEventListeners() {
        // Evento onItemClick - Ver detalles del usuario
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Usuario usuario = usuarios.get(position);
                    mostrarDetalles(usuario);
                } catch (Exception e) {
                    Toast.makeText(ListadoActivity.this, 
                        "Error al mostrar detalles: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Evento onItemLongClick - Eliminar usuario
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, 
                    int position, long id) {
                try {
                    Usuario usuario = usuarios.get(position);
                    confirmarEliminacion(usuario, position);
                    return true;
                } catch (Exception e) {
                    Toast.makeText(ListadoActivity.this, 
                        "Error al procesar eliminación: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });

        // Evento onClick del botón refrescar
        btnRefrescar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarDatos();
                Toast.makeText(ListadoActivity.this, "Datos actualizados", 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Muestra los detalles de un usuario en un diálogo
     */
    private void mostrarDetalles(Usuario usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detalles de " + usuario.getNombre());
        
        String detalles = "Email: " + usuario.getEmail() + "\n" +
                         "Teléfono: " + usuario.getTelefono() + "\n" +
                         "Edad: " + usuario.getEdad() + "\n" +
                         "Ciudad: " + usuario.getCiudad() + "\n" +
                         "Género: " + usuario.getGenero() + "\n" +
                         "Notificaciones: " + (usuario.isNotificaciones() ? "Sí" : "No");
        
        builder.setMessage(detalles);
        builder.setPositiveButton("Cerrar", null);
        builder.show();
    }

    /**
     * Confirma la eliminación de un usuario
     */
    private void confirmarEliminacion(Usuario usuario, int position) {
        new AlertDialog.Builder(this)
            .setTitle("Eliminar usuario")
            .setMessage("¿Estás seguro de eliminar a " + usuario.getNombre() + "?")
            .setPositiveButton("Eliminar", (dialog, which) -> eliminarUsuario(usuario, position))
            .setNegativeButton("Cancelar", null)
            .show();
    }

    /**
     * Elimina un usuario de la base de datos
     */
    private void eliminarUsuario(Usuario usuario, int position) {
        try {
            boolean resultado = dbHelper.eliminarUsuario(usuario.getId());
            
            if (resultado) {
                usuarios.remove(position);
                adapter.notifyDataSetChanged();
                tvTotal.setText("Total de registros: " + usuarios.size());
                Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
            } else {
                throw new SQLException("No se pudo eliminar el usuario");
            }

        } catch (SQLException e) {
            Toast.makeText(this, "Error al eliminar: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error general: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}