package org.wit.movie.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_movie.view.*
import kotlinx.android.synthetic.main.card_movie.view.*
import kotlinx.android.synthetic.main.card_movie.view.movieTitle
import org.wit.movie.R
import org.wit.movie.helpers.readImageFromPath
import org.wit.movie.models.MovieModel
import java.util.*
import android.widget.Filter
import android.widget.Filterable
import kotlin.collections.ArrayList

interface MovieListener {
    fun onMovieClick(movie: MovieModel)
}

class MovieAdapter constructor(private var movies: List<MovieModel>,
                                   private val listener: MovieListener) : RecyclerView.Adapter<MovieAdapter.MainHolder>(), Filterable {

    var movieFilterList = ArrayList<MovieModel>()

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    for (i in movies.indices) {
                        movieFilterList.add(movies[i])
                    }
                } else {
                    val resultList = ArrayList<MovieModel>()
                    for (row in movies) {
                        if (row.title.toLowerCase(Locale.ROOT)
                                        .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    movieFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = movieFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                movieFilterList = results?.values as ArrayList<MovieModel>
                notifyDataSetChanged()
            }

        }
    }

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