package ir.co.common.dto

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class MovieModel(
    @Keep val imdbID: String,
    @SerializedName("Title") val title: String,
    @SerializedName("Year") val year: String,
    @SerializedName("Type") val type: String,
    @SerializedName("Poster") val poster: String
) : Parcelable {
    class DiffUtils : DiffUtil.ItemCallback<MovieModel>() {

        override fun areItemsTheSame(
            p0: MovieModel,
            p1: MovieModel
        ): Boolean = p0.imdbID == p1.imdbID

        override fun areContentsTheSame(
            p0: MovieModel,
            p1: MovieModel
        ): Boolean = false
    }
}

@Keep
data class MoviesResponse(
    @SerializedName("Search") val search: MutableList<MovieModel>,
    @Keep val totalResults: String
)

data class MoviesResponseLive(
    val data: LiveData<PagedList<MovieModel>>,
    val onEmptyList: LiveData<EmptyMessage>,
    val networkState: LiveData<NetworkState>,
    val endListMessage: LiveData<EmptyMessage>
)


@Keep
data class MovieInfoResponse(
    @SerializedName("Title") val title: String,
    @SerializedName("Year") val year: String,
    @SerializedName("Rated") val rated: String,
    @SerializedName("Released") val released: String,
    @SerializedName("Runtime") val runtime: String,
    @SerializedName("Genre") val genre: String,
    @SerializedName("Director") val director: String,
    @SerializedName("Writer") val writer: String,
    @SerializedName("Actors") val actors: String,
    @SerializedName("Plot") val plot: String,
    @SerializedName("Language") val language: String,
    @SerializedName("Country") val country: String,
    @SerializedName("Awards") val awards: String,
    @SerializedName("Poster") val poster: String,
    @SerializedName("Ratings") val ratings: Any,
    @SerializedName("Metascore") val metaScore: String,
    @Keep val imdbRating: String,
    @Keep val imdbVotes: String,
    @Keep val imdbID: String,
    @SerializedName("Type") val type: String,
    @SerializedName("DVD") val dvd: String,
    @SerializedName("BoxOffice") val boxOffice: String,
    @SerializedName("Production") val production: String,
    @SerializedName("Website") val website: String,
    @SerializedName("Response") val response: String
)
