package org.wit.movie.main

import android.app.Application
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.movie.models.MovieJSONStore
import org.wit.movie.models.MovieStore

class MainApp : Application(), AnkoLogger {

    lateinit var movies: MovieStore

    override fun onCreate() {
        super.onCreate()
        movies = MovieJSONStore(applicationContext)

        info("Disney Movies App started")
    }
}