package org.wit.movie.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_movie.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.movie.R
import org.wit.movie.helpers.readImage
import org.wit.movie.helpers.readImageFromPath
import org.wit.movie.helpers.showImagePicker
import org.wit.movie.main.MainApp
import org.wit.movie.models.Location
import org.wit.movie.models.MovieModel

class MovieActivity : AppCompatActivity(), AnkoLogger {

    var movie = MovieModel()
    lateinit var app : MainApp
    var edit = false
    val IMAGE_REQUEST = 1
    val LOCATION_REQUEST = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
        app = application as MainApp

        if (intent.hasExtra("Movie Edit")) {
            edit = true
            movie = intent.extras?.getParcelable<MovieModel>("Movie Edit")!!
            movieTitle.setText(movie.title)
            movieDescription.setText(movie.description)
            movieImage.setImageBitmap(readImageFromPath(this, movie.image))
            if (movie.image != null) {
                chooseImage.setText(R.string.change_movie_image)
            }
            btnAdd.setText(R.string.save_movie)
        }

        movieLocation.setOnClickListener {
            val location = Location(52.245696, -7.139102, 15f)
            if (movie.zoom != 0f) {
                location.lat =  movie.lat
                location.lng = movie.lng
                location.zoom = movie.zoom
            }
            startActivityForResult(intentFor<MapsActivity>().putExtra("Location", location), LOCATION_REQUEST)
        }

        btnAdd.setOnClickListener() {
            movie.title = movieTitle.text.toString()
            movie.year = movieYear.text.toString().toInt()
            movie.director = movieDirector.text.toString()
            movie.description = movieDescription.text.toString()
            if (movie.title.isEmpty()) {
                toast(R.string.enter_movie_title)
            } else {
                if (movie.year < 1937 || movie.year > 2021) {
                    toast(R.string.enter_movie_year)
                }
                if (movie.director.isEmpty()) {
                    toast(R.string.enter_movie_director)
                }
                if (edit) {
                    app.movies.update(movie.copy())
                } else {
                    app.movies.create(movie.copy())
                }
            }
            info("Add Button Pressed: $movieTitle")
            setResult(AppCompatActivity.RESULT_OK)
            finish()
        }

        toolbarAdd.title = title
        setSupportActionBar(toolbarAdd)

        chooseImage.setOnClickListener {
            showImagePicker(this, IMAGE_REQUEST)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_movie, menu)
        if (edit && menu != null) menu.getItem(0).setVisible(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.item_delete -> {
                app.movies.delete(movie)
                finish()
            }
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IMAGE_REQUEST -> {
                if (data != null) {
                    movie.image = data.getData().toString()
                    movieImage.setImageBitmap(readImage(this, resultCode, data))
                    chooseImage.setText(R.string.change_movie_image)
                }
            }
            LOCATION_REQUEST -> {
                if (data != null) {
                    val location = data.extras?.getParcelable<Location>("Location")!!
                    movie.lat = location.lat
                    movie.lng = location.lng
                    movie.zoom = location.zoom
                }
            }
        }
    }
}