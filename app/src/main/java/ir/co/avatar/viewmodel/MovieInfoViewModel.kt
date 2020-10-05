package ir.co.avatar.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ir.co.common.base.AbsBaseViewModel
import ir.co.common.dto.MovieInfoResponse
import ir.co.common.dto.MovieModel
import ir.co.common.helper.SettingManager
import ir.co.repository.repositories.MoviesRepository
import kotlinx.coroutines.launch

abstract class MovieInfoViewModel : AbsBaseViewModel() {
    val movie: MutableLiveData<MovieModel> = MutableLiveData()
    val movieInfo: MutableLiveData<MovieInfoResponse> = MutableLiveData()

    abstract fun getMovieInfo(movieId: MovieModel)
}

class MovieInfoViewModelImpl(
    private val settingManager: SettingManager,
    private val moviesRepository: MoviesRepository
) : MovieInfoViewModel() {
    private var _movie: MovieModel? = null

    override fun getMovieInfo(movie: MovieModel) {
        this._movie = movie
        val data = settingManager.getMovieInfo(movieId = movie.imdbID)
        if (data != null) {
            handleMovieInfoResponse(data)
            return
        }
        showProgressAction()
        uiScope.launch {
            try {
                handleMovieInfoResponse(moviesRepository.movieInfo(movie.imdbID))
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun handleMovieInfoResponse(data: MovieInfoResponse) {
        settingManager.setMovieInfo(data)
        hideProgressAction()
        movieInfo.postValue(data)
    }

    override fun onCreateDone() {
    }

    override fun onRetry() {
        _movie?.let {
            getMovieInfo(it)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val settingManager: SettingManager,
        private val moviesRepository: MoviesRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MovieInfoViewModelImpl(
                settingManager = settingManager,
                moviesRepository = moviesRepository
            ) as T
        }
    }
}