package com.example.movies.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.modelMovies.Movie
import com.example.movies.repository.SingletonConfiguration
import com.squareup.picasso.Picasso

class MoviesAdapter : RecyclerView.Adapter<ViewHolderMovie>() {
    val list = mutableListOf<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMovie {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler, parent, false)
        return ViewHolderMovie(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolderMovie, position: Int) {
        val movie = list[position]
        val configuration = SingletonConfiguration.config
        val imageUrl = "${configuration?.images?.base_url}${configuration?.images?.backdrop_sizes?.last()}${movie.backdrop_path}"
        Picasso.get().load(imageUrl).into(holder.image)
        holder.name.text = movie.title
    }

   fun addMovies(movies: List<Movie>){
       list.addAll(movies)
       notifyDataSetChanged()
   }
}
