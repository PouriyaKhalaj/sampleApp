package ir.co.repository.db.users

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import ir.co.common.dto.User

/**
 * Created by pooriettaw on 17,October,2020
 */
//@Entity
//data class UserEntity(
//    @PrimaryKey val id: Long,
//    @ColumnInfo(name = "first_name") val firstName: String,
//    @ColumnInfo(name = "last_name") val lastName: String
//)

@Dao
interface UsersDao {
    @Query("SELECT * FROM users")
    fun getUsers(): DataSource.Factory<Int, User>
//    fun getUsers(): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE id IN (:userId)")
    fun getUser(userId: Long): LiveData<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Delete
    fun deleteUser(user: User)
}