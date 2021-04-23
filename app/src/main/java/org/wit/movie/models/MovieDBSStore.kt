package org.wit.movie.models

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.content.ContentValues
import org.wit.movie.models.MovieStore


class MovieDBStore(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int)
    : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION), MovieStore
{

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_MOVIES_TABLE = ("CREATE TABLE " +
                TABLE_MOVIES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_TITLE
                + " TEXT," + COLUMN_DESCRIPTION + " TEXT" + ")")
        db.execSQL(CREATE_MOVIES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIES")
        onCreate(db)
    }

    companion object {

        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "movieDB.db"
        val TABLE_MOVIES = "movies"

        val COLUMN_ID = "_id"
        val COLUMN_TITLE = "title"
        val COLUMN_DESCRIPTION = "description"
    }

    override fun findAll(): List<MovieModel> {
        val query = "SELECT * FROM $TABLE_MOVIES"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        val movies = ArrayList<MovieModel>()

        if (cursor.moveToFirst()) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val id = Integer.parseInt(cursor.getString(0)).toLong()
                val title = cursor.getString(1)
                val description = cursor.getString(2)
                movies.add(MovieModel(id, title = title, description = description))
                cursor.moveToNext()
            }
            cursor.close()
        }
        db.close()
        return movies
    }

    override fun create(movie: MovieModel) {
        val values = ContentValues()
        values.put(COLUMN_TITLE, movie.title)
        values.put(COLUMN_DESCRIPTION, movie.description)

        val db = this.writableDatabase

        db.insert(TABLE_MOVIES, null, values)
        db.close()
    }

    override fun search(searchTerm: String): List<MovieModel> {
        val query = "SELECT * FROM $TABLE_MOVIES WHERE $COLUMN_TITLE.contains(\"$searchTerm\")"

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)

        var movies: List<MovieModel> = emptyList()

        if (cursor.moveToFirst()) {
            cursor.moveToFirst()

            val id = Integer.parseInt(cursor.getString(0)).toLong()
            val title = cursor.getString(1)
            val description = cursor.getString(2)
            movies.toMutableList().add(MovieModel(id, title = title, description = description))
            cursor.close()
        }

        db.close()
        return movies
    }

    override fun update(movie: MovieModel) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, movie.title)
        values.put(COLUMN_DESCRIPTION, movie.description)


        db.update(TABLE_MOVIES, values, COLUMN_ID + " = ?", null)
        db.close()
    }

    override fun delete(movie: MovieModel) {
        val query =
                "SELECT * FROM $TABLE_MOVIES WHERE $COLUMN_TITLE = \"$movie.title\""

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val id = Integer.parseInt(cursor.getString(0))
            db.delete(TABLE_MOVIES, COLUMN_ID + " = ?",
                    arrayOf(id.toString()))
            cursor.close()
        }
        db.close()
    }

}