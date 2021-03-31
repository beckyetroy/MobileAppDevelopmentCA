package org.wit.movie.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_movie.view.*
import org.wit.movie.R
import org.wit.movie.helpers.readImageFromPath
import org.wit.movie.models.MovieModel

interface MovieListener {
    fun onMovieClick(movie: MovieModel)
}

class MovieAdapter constructor(private var movies: List<MovieModel>,
                                   private val listener: MovieListener) : RecyclerView.Adapter<MovieAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(LayoutInflater.from(parent?.context).inflate(R.layout.card_movie, parent, false))
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val movie = movies[holder.adapterPosition]
        holder.bind(movie, listener)
    }

    override fun getItemCount(): Int = movies.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(movie: MovieModel, listener : MovieListener) {
            itemView.movieTitle.text = movie.title
            itemView.description.text = movie.description
            itemView.imageIcon.setImageBitmap(readImageFromPath(itemView.context, movie.image))
            itemView.setOnClickListener { listener.onMovieClick(movie) }
        }
    }
}