package org.wit.movie.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_movie_list.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivityForResult
import org.wit.movie.R
import org.wit.movie.main.MainApp
import org.wit.movie.models.MovieModel

class MovieListActivity : AppCompatActivity(), MovieListener {

    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        app = application as MainApp

        //enable action bar and set title
        toolbar.title = title
        setSupportActionBar(toolbar)

        //layout and populate for display
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        loadMovies()


        movie_search.setOnQueryTextListener(object: SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    showMovies(app.movies.search(newText))
                }
                else {

                }
                return false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> startActivityForResult<MovieActivity>(0)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMovieClick(movie: MovieModel) {
        startActivityForResult(intentFor<MovieActivity>().putExtra("movie_edit", movie), 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loadMovies()
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadMovies() {
        showMovies(app.movies.findAll())
    }

    fun showMovies(movies: List<MovieModel>) {
        recyclerView.adapter = MovieAdapter(movies, this)
        recyclerView.adapter?.notifyDataSetChanged()
    }
}

