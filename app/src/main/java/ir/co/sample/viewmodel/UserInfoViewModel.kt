package ir.co.sample.viewmodel

import androidx.lifecycle.*
import ir.co.common.base.AbsBaseViewModel
import ir.co.common.dto.User
import ir.co.common.helper.SettingManager
import ir.co.common.utils.SingleLiveEvent
import ir.co.repository.db.users.UsersLocalCache
import ir.co.repository.repositories.UsersRepository

abstract class UserInfoViewModel : AbsBaseViewModel() {
    val userInfo: MutableLiveData<User> = MutableLiveData()
    val sendMail: SingleLiveEvent<String> = SingleLiveEvent()
    val bookmarked: MediatorLiveData<Boolean> = MediatorLiveData()

    abstract fun setUser(user: User)
    abstract fun onBookmarkClicked()
    abstract fun onMessageClicked()
}

class UserInfoViewModelImpl(
    private val settingManager: SettingManager,
    private val usersLocalCache: UsersLocalCache,
    private val usersRepository: UsersRepository
) : UserInfoViewModel() {
    private var _sourceUser: MutableLiveData<LiveData<User>> = MutableLiveData()
    private var _user: User? = null
    private var _isBookmark = false
    private var _isAddedSource = false

    init {
        bookmarked.addSource(_sourceUser) {
            if (!_isAddedSource)
                bookmarked.addSource(it) { userEntity ->
                    _isAddedSource = true
                    _isBookmark = userEntity != null
                    bookmarked.postValue(_isBookmark)
                }
        }
    }

    override fun setUser(user: User) {
        this._user = user
        userInfo.postValue(user)
        requestUserById(user.id)
    }

    override fun onBookmarkClicked() {
        _user?.let {
            if (!_isBookmark) {
                usersLocalCache.insertUser(it)
            } else {
                usersLocalCache.deleteUser(it)
            }
            requestUserById(it.id)
        }
    }

    private fun requestUserById(userId: Long) {
        _sourceUser.postValue(usersLocalCache.getUser(userId))
    }

    override fun onMessageClicked() {
        sendMail.postValue(_user?.email)
    }

    override fun onCreateDone() {
    }

    override fun onRetry() {
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val settingManager: SettingManager,
        private val usersLocalCache: UsersLocalCache,
        private val usersRepository: UsersRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return UserInfoViewModelImpl(
                settingManager = settingManager,
                usersLocalCache = usersLocalCache,
                usersRepository = usersRepository
            ) as T
        }
    }
}