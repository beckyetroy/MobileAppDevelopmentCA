package org.wit.movie.models

interface MovieStore {
    fun findAll(): List<MovieModel>
    fun create(movie: MovieModel)
    fun search(searchTerm: String): List<MovieModel>
    fun update(movie: MovieModel)
    fun delete(movie: MovieModel)

}