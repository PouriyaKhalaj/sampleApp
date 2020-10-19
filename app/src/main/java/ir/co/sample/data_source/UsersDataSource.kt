package ir.co.sample.data_source


import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import ir.co.common.base.BaseDataSource
import ir.co.common.dto.EmptyMessage
import ir.co.common.dto.NetworkState
import ir.co.common.dto.User
import ir.co.common.dto.UsersResponse
import ir.co.common.helper.SettingManager
import ir.co.common.utils.SingleLiveEvent
import ir.co.repository.repositories.UsersRepository
import kotlinx.coroutines.launch


class UsersDataSource(
    private val pageSize: Int,
    private val query: String,
    private val settingManager: SettingManager,
    private val usersRepository: UsersRepository
) : BaseDataSource<User>() {

    private val networkState: SingleLiveEvent<NetworkState> = SingleLiveEvent()
    private val onEmptyList: SingleLiveEvent<EmptyMessage> = SingleLiveEvent()
    override fun getNetworkEventShow(): SingleLiveEvent<NetworkState> = networkState

    override fun getEmptyList(): SingleLiveEvent<EmptyMessage> = onEmptyList

    private val onEndListMessage: SingleLiveEvent<EmptyMessage> = SingleLiveEvent()
    override fun endListMessage(): SingleLiveEvent<EmptyMessage> = onEndListMessage

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, User>
    ) {
        super.loadInitial(params, callback)
        searchUsers(lastRequestedPage) { data ->
            if (data != null)
                callback.onResult(data.data, lastRequestedPage, lastRequestedPage++)
        }
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, User>
    ) {
        super.loadAfter(params, callback)
        searchUsers(params.key + 1) { data ->
            if (data != null)
                callback.onResult(data.data, lastRequestedPage)
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, User>
    ) {
        super.loadBefore(params, callback)
    }


    private fun searchUsers(
        page: Int,
        onLoadData: (response: UsersResponse?) -> Unit
    ) {
        if (isRequestInProgress || !haveNextPage) return
        isRequestInProgress = true

        showProgressAction()

        uiScope.launch {
            try {
                val data = usersRepository.users(page)
                handleResponseData(data.perPage, data.data.size, page)
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
        private val usersRepository: UsersRepository
    ) : DataSource.Factory<Int, User>() {

        val liveDataSource = MutableLiveData<UsersDataSource>()

        override fun create(): DataSource<Int, User> {

            val source = UsersDataSource(
                pageSize = pageSize,
                query = query,
                settingManager = settingManager,
                usersRepository = usersRepository
            )

            liveDataSource.postValue(source)
            return source
        }
    }
}