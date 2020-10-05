package ir.co.avatar.viewmodel

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ir.co.avatar.data_source.MoviesDataSource
import ir.co.common.base.AbsBaseViewModel
import ir.co.common.dto.MovieModel
import ir.co.common.dto.MoviesResponseLive
import ir.co.common.helper.SettingManager
import ir.co.repository.repositories.MoviesRepository

abstract class MoviesViewModel : AbsBaseViewModel() {
    val phoneNumber: MutableLiveData<String> = MutableLiveData()

    abstract fun getMovies(): LiveData<PagedList<MovieModel>>
    abstract fun onRefresh()
}

class MoviesViewModelImpl(
    private val settingManager: SettingManager,
    private val moviesRepository: MoviesRepository
) : MoviesViewModel() {
    private val query = MutableLiveData<String>()

    private val repoMovies = Transformations.map(query) {
        searchMovies(it)
    }


    override fun getMovies(): LiveData<PagedList<MovieModel>> =
        Transformations.switchMap(repoMovies) {
            it.data
        }

    init {
        network = Transformations.switchMap(repoMovies) { it.networkState }
        onEmptyList = Transformations.switchMap(repoMovies) { it.onEmptyList }
        onEndListMessage = Transformations.switchMap(repoMovies) { it.endListMessage }
    }


    lateinit var customerDataSource: MoviesDataSource.Factory

    private fun searchMovies(query: String): MoviesResponseLive {
        customerDataSource = MoviesDataSource.Factory(
            pageSize = 10,
            query = query,
            settingManager = settingManager,
            moviesRepository = moviesRepository
        )

        val data = LivePagedListBuilder(
            customerDataSource,
            10
        ).build()
        return MoviesResponseLive(
            data = data,
            onEmptyList = Transformations.switchMap(customerDataSource.liveDataSource) { it.getEmptyList() },
            networkState = Transformations.switchMap(customerDataSource.liveDataSource) { it.getNetworkEventShow() },
            endListMessage = Transformations.switchMap(customerDataSource.liveDataSource) { it.endListMessage() }
        )
    }

    override fun onRefresh() {
        if (::customerDataSource.isInitialized)
            customerDataSource.liveDataSource.value?.invalidate()
    }

    override fun onCreateDone() {
        query.postValue("avatar")
    }


    override fun onRetry() {
        if (::customerDataSource.isInitialized)
            customerDataSource.liveDataSource.value?.onRetry()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val settingManager: SettingManager,
        private val moviesRepository: MoviesRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MoviesViewModelImpl(
                settingManager = settingManager,
                moviesRepository = moviesRepository
            ) as T
        }
    }

}