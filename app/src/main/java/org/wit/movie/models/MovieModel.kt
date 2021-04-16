package org.wit.movie.models

import android.media.Rating
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieModel(var id: Long = 0,
                      var title: String = "",
                      var year: Int = 0,
                      var director: String = "",
                      var description: String = "",
                      var image: String = "",
                      var rating: Float = 0.0f,
                      var lat : Double = 0.0,
                      var lng: Double = 0.0,
                      var zoom: Float = 0f) : Parcelable

@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable