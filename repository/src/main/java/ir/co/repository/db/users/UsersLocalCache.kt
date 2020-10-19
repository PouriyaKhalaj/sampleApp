package ir.co.repository.db.users

import ir.co.common.dto.User
import javax.inject.Inject
import kotlin.concurrent.thread

/**
 * Created by pooriettaw on 17,October,2020
 */
class UsersLocalCache @Inject constructor(@JvmSuppressWildcards val usersDao: UsersDao) : UsersDao {

    override fun getUsers() = usersDao.getUsers()

    override fun getUser(userId: Long) = usersDao.getUser(userId)

    override fun insertUser(user: User) {
        thread {
            usersDao.insertUser(user)
        }
    }

    override fun deleteUser(user: User) {
        thread {
            usersDao.deleteUser(user)
        }
    }
}