package ir.co.sample.viewmodel

import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ir.co.common.base.AbsBaseViewModel
import ir.co.common.dto.User
import ir.co.common.dto.UsersResponseLive
import ir.co.common.helper.SettingManager
import ir.co.common.utils.SingleLiveEvent
import ir.co.repository.db.users.UsersLocalCache
import ir.co.repository.repositories.UsersRepository
import ir.co.sample.data_source.UsersDataSource

abstract class UsersViewModel : AbsBaseViewModel() {
    val bookmarkMode: MediatorLiveData<Boolean> = MediatorLiveData()

    abstract fun getUsers(): LiveData<PagedList<User>>
    abstract fun getUsersFromDb(): LiveData<PagedList<User>>
    abstract fun onRefresh()
    abstract fun onBookmarkClicked()
}

class UsersViewModelImpl(
    private val settingManager: SettingManager,
    private val usersLocalCache: UsersLocalCache,
    private val usersRepository: UsersRepository
) : UsersViewModel() {
    private lateinit var customerDataSource: UsersDataSource.Factory

    private val query = SingleLiveEvent<String>()
    private val bookmarkQuery = SingleLiveEvent<Boolean>()
    private var _bookmarkMode = false

    private val repoMovies = Transformations.map(query) { searchMovies(it) }

    init {
        network = Transformations.switchMap(repoMovies) { it.networkState }
        onEmptyList = Transformations.switchMap(repoMovies) { it.onEmptyList }
        onEndListMessage = Transformations.switchMap(repoMovies) { it.endListMessage }
    }

    override fun onBackPressedClicked() {
        _bookmarkMode = false
        bookmarkMode.postValue(false)
        bookmarkQuery.postValue(null)
        onRefresh()
    }

    override fun getUsers(): LiveData<PagedList<User>> =
        Transformations.switchMap(repoMovies) {
            if (_bookmarkMode) MutableLiveData(null)
            else it.data
        }


    override fun getUsersFromDb(): LiveData<PagedList<User>> =
        Transformations.switchMap(bookmarkQuery) {
            if (_bookmarkMode) getLiveDataPagedList()
            else MutableLiveData(null)
        }


    private fun searchMovies(query: String): UsersResponseLive {
        customerDataSource = UsersDataSource.Factory(
            pageSize = 10,
            query = query,
            settingManager = settingManager,
            usersRepository = usersRepository
        )

        val data = LivePagedListBuilder(
            customerDataSource,
            10
        ).build()
        return UsersResponseLive(
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


    override fun onBookmarkClicked() {
        _bookmarkMode = true
        bookmarkQuery.postValue(true)
        bookmarkMode.postValue(true)
    }

    private fun getLiveDataPagedList(): LiveData<PagedList<User>> {
        val pagedListConfig =
            PagedList.Config.Builder()
                .setPrefetchDistance(5)
                .setPageSize(20)
                .setInitialLoadSizeHint(20)
                .setEnablePlaceholders(false)
                .build()

        return LivePagedListBuilder(
            getAllReposPagedFactory(),
            pagedListConfig
        ).build()
    }

    private fun getAllReposPagedFactory(): DataSource.Factory<Int, User> =
        usersLocalCache.getUsers()

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
        private val usersLocalCache: UsersLocalCache,
        private val usersRepository: UsersRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return UsersViewModelImpl(
                settingManager = settingManager,
                usersLocalCache = usersLocalCache,
                usersRepository = usersRepository
            ) as T
        }
    }

}