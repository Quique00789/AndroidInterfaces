package com.example.androidinterfaces.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.androidinterfaces.models.Usuario;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper de base de datos SQLite
 * Implementa CRUD completo con manejo de excepciones
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AndroidInterfaces.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla Usuarios
    private static final String TABLE_USUARIOS = "usuarios";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_TELEFONO = "telefono";
    private static final String COLUMN_EDAD = "edad";
    private static final String COLUMN_CIUDAD = "ciudad";
    private static final String COLUMN_GENERO = "genero";
    private static final String COLUMN_NOTIFICACIONES = "notificaciones";

    // Query de creación de tabla
    private static final String CREATE_TABLE_USUARIOS = 
        "CREATE TABLE " + TABLE_USUARIOS + " (" +
        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_NOMBRE + " TEXT NOT NULL, " +
        COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
        COLUMN_TELEFONO + " TEXT, " +
        COLUMN_EDAD + " INTEGER, " +
        COLUMN_CIUDAD + " TEXT, " +
        COLUMN_GENERO + " TEXT, " +
        COLUMN_NOTIFICACIONES + " INTEGER DEFAULT 1)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_USUARIOS);
        } catch (android.database.SQLException e) {
            throw new RuntimeException("Error al crear la base de datos: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
            onCreate(db);
        } catch (android.database.SQLException e) {
            throw new RuntimeException("Error al actualizar la base de datos: " + e.getMessage());
        }
    }

    /**
     * CREATE - Inserta un nuevo usuario en la base de datos
     * @return ID del usuario insertado o -1 si hubo error
     */
    public long insertarUsuario(Usuario usuario) throws SQLException {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            
            values.put(COLUMN_NOMBRE, usuario.getNombre());
            values.put(COLUMN_EMAIL, usuario.getEmail());
            values.put(COLUMN_TELEFONO, usuario.getTelefono());
            values.put(COLUMN_EDAD, usuario.getEdad());
            values.put(COLUMN_CIUDAD, usuario.getCiudad());
            values.put(COLUMN_GENERO, usuario.getGenero());
            values.put(COLUMN_NOTIFICACIONES, usuario.isNotificaciones() ? 1 : 0);

            long resultado = db.insert(TABLE_USUARIOS, null, values);
            
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

    /**
     * READ - Obtiene todos los usuarios
     */
    public List<Usuario> obtenerTodosUsuarios() throws SQLException, IOException {
        List<Usuario> usuarios = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_USUARIOS, null, null, null, null, null, 
                COLUMN_NOMBRE + " ASC");

            if (cursor == null) {
                throw new IOException("Error al obtener cursor de la base de datos");
            }

            if (cursor.moveToFirst()) {
                do {
                    Usuario usuario = new Usuario();
                    usuario.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                    usuario.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)));
                    usuario.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                    usuario.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONO)));
                    usuario.setEdad(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EDAD)));
                    usuario.setCiudad(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CIUDAD)));
                    usuario.setGenero(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENERO)));
                    usuario.setNotificaciones(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICACIONES)) == 1);
                    
                    usuarios.add(usuario);
                } while (cursor.moveToNext());
            }

            return usuarios;

        } catch (android.database.SQLException e) {
            throw new SQLException("Error SQL al leer: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new SQLException("Error en columnas de la BD: " + e.getMessage());
        } catch (Exception e) {
            throw new IOException("Error de I/O al leer datos: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * READ - Obtiene un usuario por ID
     */
    public Usuario obtenerUsuarioPorId(int id) throws SQLException {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_USUARIOS, null, 
                COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, 
                null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                Usuario usuario = new Usuario();
                usuario.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                usuario.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)));
                usuario.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                usuario.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONO)));
                usuario.setEdad(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EDAD)));
                usuario.setCiudad(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CIUDAD)));
                usuario.setGenero(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENERO)));
                usuario.setNotificaciones(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICACIONES)) == 1);
                return usuario;
            }

            throw new SQLException("Usuario no encontrado con ID: " + id);

        } catch (android.database.SQLException e) {
            throw new SQLException("Error SQL al buscar usuario: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * UPDATE - Actualiza un usuario existente
     */
    public boolean actualizarUsuario(Usuario usuario) throws SQLException {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            
            values.put(COLUMN_NOMBRE, usuario.getNombre());
            values.put(COLUMN_EMAIL, usuario.getEmail());
            values.put(COLUMN_TELEFONO, usuario.getTelefono());
            values.put(COLUMN_EDAD, usuario.getEdad());
            values.put(COLUMN_CIUDAD, usuario.getCiudad());
            values.put(COLUMN_GENERO, usuario.getGenero());
            values.put(COLUMN_NOTIFICACIONES, usuario.isNotificaciones() ? 1 : 0);

            int filasAfectadas = db.update(TABLE_USUARIOS, values, 
                COLUMN_ID + " = ?", new String[]{String.valueOf(usuario.getId())});

            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo actualizar el usuario");
            }

            return filasAfectadas > 0;

        } catch (android.database.SQLException e) {
            throw new SQLException("Error SQL al actualizar: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * DELETE - Elimina un usuario por ID
     */
    public boolean eliminarUsuario(int id) throws SQLException {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            int filasEliminadas = db.delete(TABLE_USUARIOS, 
                COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

            if (filasEliminadas == 0) {
                throw new SQLException("No se encontró el usuario a eliminar");
            }

            return filasEliminadas > 0;

        } catch (android.database.SQLException e) {
            throw new SQLException("Error SQL al eliminar: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * Busca usuarios por nombre
     */
    public List<Usuario> buscarUsuariosPorNombre(String nombre) throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_USUARIOS, null, 
                COLUMN_NOMBRE + " LIKE ?", new String[]{"%" + nombre + "%"}, 
                null, null, COLUMN_NOMBRE + " ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Usuario usuario = new Usuario();
                    usuario.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                    usuario.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)));
                    usuario.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                    usuario.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONO)));
                    usuario.setEdad(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EDAD)));
                    usuario.setCiudad(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CIUDAD)));
                    usuario.setGenero(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENERO)));
                    usuario.setNotificaciones(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICACIONES)) == 1);
                    
                    usuarios.add(usuario);
                } while (cursor.moveToNext());
            }

            return usuarios;

        } catch (android.database.SQLException e) {
            throw new SQLException("Error SQL en búsqueda: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * Obtiene el conteo total de usuarios
     */
    public int contarUsuarios() throws SQLException {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USUARIOS, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }

            return 0;

        } catch (android.database.SQLException e) {
            throw new SQLException("Error SQL al contar: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }
}