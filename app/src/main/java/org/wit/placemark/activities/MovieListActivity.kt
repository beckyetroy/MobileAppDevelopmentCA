package org.wit.placemark.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_movie_list.*
import org.jetbrains.anko.intentFor
import org.wit.placemark.R
import org.wit.placemark.main.MainApp
import org.jetbrains.anko.startActivityForResult
import org.wit.placemark.models.PlacemarkModel

class MovieListActivity : AppCompatActivity(), PlacemarkListener {

    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        app = application as MainApp

        //layout and populate for display
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager   //recyclerView is a widget in activity_placemark_list.xml
        loadPlacemarks()

        //enable action bar and set title
        toolbar.title = title
        setSupportActionBar(toolbar)
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

    override fun onPlacemarkClick(placemark: PlacemarkModel) {
        startActivityForResult(intentFor<MovieActivity>().putExtra("placemark_edit", placemark), 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loadPlacemarks()
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadPlacemarks() {
        showPlacemarks(app.placemarks.findAll())
    }

    fun showPlacemarks (placemarks: List<PlacemarkModel>) {
        recyclerView.adapter = PlacemarkAdapter(placemarks, this)
        recyclerView.adapter?.notifyDataSetChanged()
    }
}

