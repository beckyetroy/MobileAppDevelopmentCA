package org.wit.placemark.main

import android.app.Application
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.placemark.models.MovieJSONStore
import org.wit.placemark.models.MovieStore

class MainApp : Application(), AnkoLogger {

    lateinit var placemarks: MovieStore

    override fun onCreate() {
        super.onCreate()
        placemarks = MovieJSONStore(applicationContext)
        info("Placemark started")
    }
}