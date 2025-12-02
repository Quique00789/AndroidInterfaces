package com.example.androidinterfaces.api;

import com.example.androidinterfaces.models.Post;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Interface de servicios API
 * Define los endpoints disponibles
 */
public interface ApiService {
    
    /**
     * Obtiene la lista de posts
     */
    @GET("posts")
    Call<List<Post>> getPosts();
    
    /**
     * Obtiene un post específico por ID
     */
    @GET("posts/{id}")
    Call<Post> getPost(@Path("id") int id);
}