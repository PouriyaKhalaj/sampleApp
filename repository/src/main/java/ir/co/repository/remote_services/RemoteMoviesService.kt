package ir.co.repository.remote_services

import ir.co.common.dto.MovieInfoResponse
import ir.co.common.dto.MoviesResponse
import ir.co.repository.BuildConfig
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteMoviesService {

    @GET("?apikey=${BuildConfig.apikey}")
    fun moviesAsync(
        @Query("s") query: String? = "avatar",
        @Query("page") page: Int
    ): Deferred<MoviesResponse>

    @GET("?apikey=${BuildConfig.apikey}")
    fun movieInfoAsync(@Query("i") imdbID: String): Deferred<MovieInfoResponse>
}