package com.example.androidinterfaces.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidinterfaces.R;
import com.example.androidinterfaces.models.Post;
import java.util.List;

/**
 * Adapter para RecyclerView de posts de API
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private Context context;
    private List<Post> posts;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.tvTitle.setText(post.getTitle());
        holder.tvBody.setText(post.getBody());
        holder.tvId.setText("ID: " + post.getId());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvBody;
        TextView tvId;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitlePost);
            tvBody = itemView.findViewById(R.id.tvBodyPost);
            tvId = itemView.findViewById(R.id.tvIdPost);
        }
    }
}