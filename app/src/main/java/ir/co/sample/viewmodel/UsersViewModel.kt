package ir.co.sample.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
    val bookmarkPage: SingleLiveEvent<Boolean> = SingleLiveEvent()

    abstract fun getUsers(): LiveData<PagedList<User>>
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

    private val repoMovies = Transformations.map(query) { searchMovies(it) }

    init {
        network = Transformations.switchMap(repoMovies) { it.networkState }
        onEmptyList = Transformations.switchMap(repoMovies) { it.onEmptyList }
        onEndListMessage = Transformations.switchMap(repoMovies) { it.endListMessage }
    }

    override fun getUsers(): LiveData<PagedList<User>> =
        Transformations.switchMap(repoMovies) { it.data }

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
        bookmarkPage.postValue(true)
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