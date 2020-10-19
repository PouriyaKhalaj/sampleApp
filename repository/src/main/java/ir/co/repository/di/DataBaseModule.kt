package ir.co.repository.di


import android.content.Context
import dagger.Module
import dagger.Provides
import ir.co.repository.db.AppDatabase
import ir.co.repository.db.users.UsersDao
import ir.co.repository.db.users.UsersLocalCache


@Module
class DataBaseModule(private val context: Context) {

    @Provides
    fun provideDataBase(): AppDatabase = AppDatabase.invoke(context)

    @Provides
    fun provideUsersDao(db: AppDatabase): UsersDao = db.getUsersDao()

    @Provides
    fun provideUserLocalRepository(usersDao: UsersDao): UsersLocalCache = UsersLocalCache(usersDao)

}