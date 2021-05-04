package org.wit.movie.activities

import android.R.id.message
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.storage.ktx.storageMetadata
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
import java.io.File


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

        //enable action bar and set title
        toolbarAdd.title = "Add Movie"
        setSupportActionBar(toolbarAdd)

        if (intent.hasExtra("movie_edit")) {
            edit = true
            movie = intent.extras?.getParcelable<MovieModel>("movie_edit")!!
            //set the title of the toolbar to the movie title
            toolbarAdd.title = movie.title
            //populate the fields
            movieTitle.setText(movie.title)
            movieYear.setText(movie.year.toString())
            movieDirector.setText(movie.director)
            movieDescription.setText(movie.description)
            rBar.setOnRatingBarChangeListener(object : RatingBar.OnRatingBarChangeListener {
                override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {
                    //if the rating is changed, update it in movie
                    movie.rating = findViewById<RatingBar>(R.id.rBar).rating
                }
            })
            //populate the rating bar to previously set rating
            findViewById<RatingBar>(R.id.rBar).setRating(movie.rating)
            //display image stored
            movieImage.setImageBitmap(readImageFromPath(this, movie.image))
            if (movie.image != null) {
                chooseImage.setText(R.string.change_movie_image)
            }
            //update location button from 'add location' to 'change location'
            movieLocation.setText(R.string.change_button_location)
            //update 'add movie' text on button to 'save movie'
            btnAdd.setText(R.string.save_movie)
            //display the IMDB button
            IMDBBtn.isVisible = true;
        }

        movieLocation.setOnClickListener {
            /*set default location (DisneyWorld, Orlando) which user can keep if they're
            not interested in this feature*/
            val location = Location(28.385233, -81.563873, -20f)
            if (movie.zoom != 0f) {
                location.lat = movie.lat
                location.lng = movie.lng
                location.zoom = movie.zoom
            }
            //start Google Maps API
            startActivityForResult(
                    intentFor<GMapsActivity>().putExtra("location", location),
                    LOCATION_REQUEST
            )
        }

        infoBtn.setOnClickListener() {
            //Display info button message as toast notification
            toast(R.string.location_info)
        }

        btnAdd.setOnClickListener() {
            //parse the fields and assign them to their relevant values
            movie.title = movieTitle.text.toString()
            var year = movieYear.text.toString().toInt()
            movie.director = movieDirector.text.toString()
            movie.description = movieDescription.text.toString()
            movie.rating = findViewById<RatingBar>(R.id.rBar).rating
            movie.year = year
            //validation
            if (movie.title.isEmpty()) {
                //title cannot be null - display error message
                toast(R.string.enter_movie_title)
            }
            //year must be between 1937 and 2021 (any other is impossible for a Disney movie)
            if (year < 1937 || year > 2021) {
                toast(R.string.enter_movie_year)
            }
            else {
                if (edit) {
                    //update the stored values
                    app.movies.update(movie.copy())
                } else {
                    //create the movie
                    app.movies.create(movie.copy())
                }
                info("Add Button Pressed: $movieTitle")
                setResult(AppCompatActivity.RESULT_OK)
                //finish the activity
                finish()
                toast("Movie saved")
            }
        }

        chooseImage.setOnClickListener {
            //open image picker
            showImagePicker(this, IMAGE_REQUEST)
        }

        IMDBBtn.setOnClickListener {
            val imdb = Intent(
                //Sends the User to the IMDB Web page
                Intent.ACTION_VIEW,
                Uri.parse("https://www.imdb.com/find?q=" + movie.title)
            )
            //starts the activity
            startActivity(imdb)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //display menu
        menuInflater.inflate(R.menu.menu_movie, menu)
        //if the movie is in edit mode, display the delete and email buttons
        if (edit && menu != null) menu.getItem(0).setVisible(true)
        if (edit && menu != null) menu.getItem(1).setVisible(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.item_email -> {
                val email = Intent(Intent.ACTION_SEND)
                //form email containing movie details
                email.putExtra(Intent.EXTRA_EMAIL, arrayOf("")) // recipients - will leave blank for users to complete
                email.putExtra(Intent.EXTRA_SUBJECT, "Check out this Disney Movie!")
                email.putExtra(Intent.EXTRA_TEXT, "Have a look at this movie: ${movie.title}" +
                        if (movie.year != null) {"\n Released in: ${movie.year}"} else {} +
                        if (movie.director != null) {"\n Directed by: ${movie.director}"} else {} +
                        "\n \n I rated it " + movie.rating + "/5.0 stars!")

                //need this to prompts email client only
                email.type = "message/rfc822"
                //starts the activity by choosing an email client, then going to send the email
                startActivity(Intent.createChooser(email, "Choose an Email client :"))
            }
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
                //display confirmation dialog
                alert.show()
            }
            R.id.item_cancel -> {
                //finish the activity
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
                    //parse image bitmap and set that as movie image attribute
                    movie.image = data.getData().toString()
                    movieImage.setImageBitmap(readImage(this, resultCode, data))
                    //Change text from add image to change image, as image has been added
                    chooseImage.setText(R.string.change_movie_image)
                }
            }
            LOCATION_REQUEST -> {
                if (data != null) {
                    //parse location of placemarker and make that the location with appropriate zoom
                    val location = data.extras?.getParcelable<Location>("location")!!
                    movie.lat = location.lat
                    movie.lng = location.lng
                    movie.zoom = location.zoom
                }
            }
        }
    }
}