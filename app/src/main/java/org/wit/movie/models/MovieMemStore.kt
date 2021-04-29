package org.wit.movie.models

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class MovieMemStore : MovieStore, AnkoLogger {

    val movies = ArrayList<MovieModel>()

    override fun findAll(): List<MovieModel> {
        return movies
    }

    override fun create(movie: MovieModel) {
        movie.id = getId()
        movies.add(movie)
        logAll()
    }

    override fun search(searchTerm: String): List<MovieModel> {
        return movies.filter { movie -> movie.title.toLowerCase().contains(searchTerm.toLowerCase()) }
    }

    override fun update(movie: MovieModel) {
        var foundMovie: MovieModel? = movies.find { m -> m.id == movie.id }
        if (foundMovie != null) {
            foundMovie.title = movie.title
            foundMovie.year = movie.year
            foundMovie.director = movie.director
            foundMovie.description = movie.description
            foundMovie.image = movie.image
            foundMovie.rating = movie.rating
            foundMovie.lat = movie.lat
            foundMovie.lng = movie.lng
            foundMovie.zoom = movie.zoom
            logAll();
        }
    }

    override fun delete(movie: MovieModel) {
        movies.remove(movie)
    }

    fun logAll() {
        movies.forEach { info("${it}") }
    }
}