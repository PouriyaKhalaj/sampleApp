package ir.co.common.helper

import ir.co.common.dto.MovieInfoResponse
import ir.co.common.dto.MovieModel
import ir.co.common.dto.MoviesResponse
import java.util.*

interface SettingManager {
    fun setMovies(hashMap: HashMap<Int, MoviesResponse>)
    fun getMovies(): HashMap<Int, MoviesResponse>?

    fun setMovieInfo(movie: MovieInfoResponse)
    fun getMovieInfo(movieId: String): MovieInfoResponse?
}