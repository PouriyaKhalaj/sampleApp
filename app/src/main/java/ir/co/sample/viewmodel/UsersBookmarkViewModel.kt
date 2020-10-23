package ir.co.sample.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ir.co.common.base.AbsBaseViewModel
import ir.co.common.dto.User
import ir.co.common.helper.SettingManager
import ir.co.common.utils.SingleLiveEvent
import ir.co.repository.db.users.UsersLocalCache

abstract class UsersBookmarkViewModel : AbsBaseViewModel() {
    abstract fun getUsersFromDb(): LiveData<PagedList<User>>

    abstract fun onRefresh()
}

class UsersBookmarkViewModelImpl(
    private val settingManager: SettingManager,
    private val usersLocalCache: UsersLocalCache
) : UsersBookmarkViewModel() {
    private val bookmarkQuery = SingleLiveEvent<Boolean>()

    override fun onCreateDone() {
        bookmarkQuery.postValue(true)
    }

    override fun onRetry() {

    }

    override fun onRefresh() {
        onCreateDone()
    }

    override fun getUsersFromDb(): LiveData<PagedList<User>> =
        Transformations.switchMap(bookmarkQuery) {
            hideProgressAction()
            getLiveDataPagedList()
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

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val settingManager: SettingManager,
        private val usersLocalCache: UsersLocalCache
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return UsersBookmarkViewModelImpl(
                settingManager = settingManager,
                usersLocalCache = usersLocalCache
            ) as T
        }
    }

}