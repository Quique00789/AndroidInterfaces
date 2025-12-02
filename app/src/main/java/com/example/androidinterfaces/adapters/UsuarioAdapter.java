package com.example.androidinterfaces.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.androidinterfaces.R;
import com.example.androidinterfaces.models.Usuario;
import java.util.List;

/**
 * Adapter personalizado para ListView de usuarios
 */
public class UsuarioAdapter extends BaseAdapter {
    private Context context;
    private List<Usuario> usuarios;
    private LayoutInflater inflater;

    public UsuarioAdapter(Context context, List<Usuario> usuarios) {
        this.context = context;
        this.usuarios = usuarios;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return usuarios.size();
    }

    @Override
    public Object getItem(int position) {
        return usuarios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return usuarios.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_usuario, parent, false);
            holder = new ViewHolder();
            holder.tvNombre = convertView.findViewById(R.id.tvNombreItem);
            holder.tvEmail = convertView.findViewById(R.id.tvEmailItem);
            holder.tvCiudad = convertView.findViewById(R.id.tvCiudadItem);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Usuario usuario = usuarios.get(position);
        holder.tvNombre.setText(usuario.getNombre());
        holder.tvEmail.setText(usuario.getEmail());
        holder.tvCiudad.setText(usuario.getCiudad());

        return convertView;
    }

    static class ViewHolder {
        TextView tvNombre;
        TextView tvEmail;
        TextView tvCiudad;
    }
}