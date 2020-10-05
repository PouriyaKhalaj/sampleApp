package ir.co.avatar.data_source


import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import ir.co.common.base.BaseDataSource
import ir.co.common.dto.EmptyMessage
import ir.co.common.dto.MovieModel
import ir.co.common.dto.MoviesResponse
import ir.co.common.dto.NetworkState
import ir.co.common.helper.SettingManager
import ir.co.common.utils.SingleLiveEvent
import ir.co.repository.repositories.MoviesRepository
import kotlinx.coroutines.launch


class MoviesDataSource(
    private val pageSize: Int,
    private val query: String,
    private val settingManager: SettingManager,
    private val moviesRepository: MoviesRepository
) : BaseDataSource<MovieModel>() {

    private val networkState: SingleLiveEvent<NetworkState> = SingleLiveEvent()
    private val onEmptyList: SingleLiveEvent<EmptyMessage> = SingleLiveEvent()
    override fun getNetworkEventShow(): SingleLiveEvent<NetworkState> = networkState

    override fun getEmptyList(): SingleLiveEvent<EmptyMessage> = onEmptyList

    private val onEndListMessage: SingleLiveEvent<EmptyMessage> = SingleLiveEvent()
    override fun endListMessage(): SingleLiveEvent<EmptyMessage> = onEndListMessage

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, MovieModel>
    ) {
        super.loadInitial(params, callback)
        searchMoviesByQuery(lastRequestedPage) { data ->
            if (data != null)
                callback.onResult(data.search, lastRequestedPage, lastRequestedPage++)
        }
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, MovieModel>
    ) {
        super.loadAfter(params, callback)
        searchMoviesByQuery(params.key + 1) { data ->
            if (data != null)
                callback.onResult(data.search, lastRequestedPage)
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, MovieModel>
    ) {
        super.loadBefore(params, callback)
    }


    private val hashMap: HashMap<Int, MoviesResponse> = hashMapOf()
    private fun searchMoviesByQuery(
        page: Int,
        onLoadData: (response: MoviesResponse?) -> Unit
    ) {
        if (isRequestInProgress || !haveNextPage) return

        settingManager.getMovies()?.let {
            val restoreResponse = it[page]
            if (restoreResponse != null) {
                handleResponseData(pageSize, restoreResponse.search.size, page)
                onLoadData(restoreResponse)
                return
            }
        }

        isRequestInProgress = true

        showProgressAction()

        uiScope.launch {
            try {
                val data = moviesRepository.movies(query, page)
                hashMap[page] = data
                settingManager.setMovies(hashMap)
                handleResponseData(pageSize, data.search.size, page)
                onLoadData(data)
            } catch (e: Exception) {
                handleError(e)
            } finally {
                isRequestInProgress = false
            }
        }
    }


    class Factory(
        private val pageSize: Int,
        private val query: String,
        private val settingManager: SettingManager,
        private val moviesRepository: MoviesRepository
    ) : DataSource.Factory<Int, MovieModel>() {

        val liveDataSource = MutableLiveData<MoviesDataSource>()

        override fun create(): DataSource<Int, MovieModel> {

            val source = MoviesDataSource(
                pageSize = pageSize,
                query = query,
                settingManager = settingManager,
                moviesRepository = moviesRepository
            )

            liveDataSource.postValue(source)
            return source
        }
    }
}