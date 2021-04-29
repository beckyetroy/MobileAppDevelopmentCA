package org.wit.movie.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.Rating
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RatingBar
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.android.synthetic.main.activity_movie_list.*
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
import java.io.IOException

class MovieActivity : AppCompatActivity(), AnkoLogger {

    var movie = MovieModel()
    lateinit var app: MainApp
    var edit = false
    val IMAGE_REQUEST = 1
    val LOCATION_REQUEST = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
        app = application as MainApp

        toolbarAdd.title = title
        setSupportActionBar(toolbarAdd)

        if (intent.hasExtra("movie_edit")) {
            edit = true
            movie = intent.extras?.getParcelable<MovieModel>("movie_edit")!!
            movieTitle.setText(movie.title)
            movieYear.setText(movie.year.toString())
            movieDirector.setText(movie.director)
            movieDescription.setText(movie.description)
            rBar.setOnRatingBarChangeListener(object : RatingBar.OnRatingBarChangeListener {
                override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {
                    movie.rating = findViewById<RatingBar>(R.id.rBar).rating
                }
            })
            findViewById<RatingBar>(R.id.rBar).setRating(movie.rating)
            movieImage.setImageBitmap(readImageFromPath(this, movie.image))
            if (movie.image != null) {
                movieImage.setImageBitmap(readImageFromPath(this, movie.image))
                chooseImage.setText(R.string.change_movie_image)
            }
            movieLocation.setText(R.string.change_button_location)
            btnAdd.setText(R.string.save_movie)
        }

        movieLocation.setOnClickListener {
            val location = Location(28.385233, -81.563873, -20f)
            if (movie.zoom != 0f) {
                location.lat = movie.lat
                location.lng = movie.lng
                location.zoom = movie.zoom
            }
            startActivityForResult(intentFor<GMapsActivity>().putExtra("location", location), LOCATION_REQUEST)
        }

        infoBtn.setOnClickListener() {
            toast(R.string.location_info)
        }

        btnAdd.setOnClickListener() {
            movie.title = movieTitle.text.toString()
            var year = movieYear.text.toString().toIntOrNull()
            movie.director = movieDirector.text.toString()
            movie.description = movieDescription.text.toString()
            movie.rating = findViewById<RatingBar>(R.id.rBar).rating
            if (movie.title.isEmpty()) {
                toast(R.string.enter_movie_title)
            }
            if (year != null) {
                if (year < 1937 || year > 2021) {
                    toast(R.string.enter_movie_year)
                } else {
                    movie.year = year;
                }
            }
            if (year == null) {
                toast(R.string.enter_movie_year)
            }
            if (movie.director.isEmpty()) {
                toast(R.string.enter_movie_director)
            } else {
                if (edit) {
                    app.movies.update(movie.copy())
                } else {
                    app.movies.create(movie.copy())
                }
                info("Add Button Pressed: $movieTitle")
                setResult(AppCompatActivity.RESULT_OK)
                finish()
                toast("Movie saved")
            }
        }
        chooseImage.setOnClickListener {
            showImagePicker(this, IMAGE_REQUEST)
        }

        IMDBBtn.setOnClickListener {
            val imdb = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/find?q=" + movie.title))
            startActivity(imdb)
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
                val builder = AlertDialog.Builder(this@MovieActivity)
                builder.setMessage(("Are you sure you want to delete this movie?"))
                        .setCancelable(false)
                        .setPositiveButton("Yes") { dialog, id ->
                            // Delete item
                            app.movies.delete(movie)
                            finish()
                        }
                        .setNegativeButton("Cancel") { dialog, id ->
                            // Dismiss the dialog
                            dialog.dismiss()
                        }
                val alert = builder.create()
                alert.show()
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
                    val location = data.extras?.getParcelable<Location>("location")!!
                    movie.lat = location.lat
                    movie.lng = location.lng
                    movie.zoom = location.zoom
                }
            }
        }
    }
}