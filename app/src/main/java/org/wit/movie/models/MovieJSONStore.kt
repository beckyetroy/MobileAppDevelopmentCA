package org.wit.movie.models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.AnkoLogger
import org.wit.movie.helpers.exists
import org.wit.movie.helpers.read
import org.wit.movie.helpers.write
import java.util.*

val JSON_FILE = "movies.json"
val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<java.util.ArrayList<MovieModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class MovieJSONStore : MovieStore, AnkoLogger {

    val context: Context
    var movies = mutableListOf<MovieModel>()

    constructor (context: Context) {
        this.context = context
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<MovieModel> {
        return movies
    }

    override fun create(movie: MovieModel) {
        movie.id = generateRandomId()
        movies.add(movie)
        serialize()
    }

    override fun update(movie: MovieModel) {
        val moviesList = findAll() as ArrayList<MovieModel>
        var foundMovie: MovieModel? = moviesList.find { m -> m.id == movie.id }
        if (foundMovie != null) {
            foundMovie.title = movie.title
            foundMovie.year = movie.year
            foundMovie.director = movie.director
            foundMovie.description = movie.description
            foundMovie.image = movie.image
            foundMovie.lat = movie.lat
            foundMovie.lng = movie.lng
            foundMovie.zoom = movie.zoom
        }
        serialize()
    }

    override fun delete(movie: MovieModel) {
        movies.remove(movie)
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(movies, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        movies = Gson().fromJson(jsonString, listType)
    }
}