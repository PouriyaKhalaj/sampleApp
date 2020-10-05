package ir.co.repository.repositories

import ir.co.common.dto.MovieInfoResponse
import ir.co.common.dto.MoviesResponse
import ir.co.repository.remote_services.RemoteMoviesService
import retrofit2.Retrofit

abstract class MoviesRepository {
    abstract suspend fun movies(query: String, page: Int): MoviesResponse
    abstract suspend fun movieInfo(imdbID: String): MovieInfoResponse
}


class MoviesRepositoryImpl constructor(private val retrofit: Retrofit) : MoviesRepository() {

    override suspend fun movies(query: String, page: Int): MoviesResponse =
        retrofit.create(RemoteMoviesService::class.java)
            .moviesAsync(query = query, page = page)
            .await()

    override suspend fun movieInfo(imdbID: String): MovieInfoResponse =
        retrofit.create(RemoteMoviesService::class.java)
            .movieInfoAsync(imdbID = imdbID)
            .await()
}