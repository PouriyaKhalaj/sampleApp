package ir.co.sample.di.users

import dagger.Module
import dagger.Provides
import ir.co.common.helper.SettingManager
import ir.co.repository.db.users.UsersLocalCache
import ir.co.repository.di.DataBaseModule
import ir.co.repository.repositories.UsersRepository
import ir.co.repository.repositories.UsersRepositoryImpl
import ir.co.sample.viewmodel.UserInfoViewModelImpl
import ir.co.sample.viewmodel.UsersBookmarkViewModelImpl
import ir.co.sample.viewmodel.UsersViewModelImpl
import retrofit2.Retrofit

@Module(includes = [DataBaseModule::class])
class UsersModule {

    @Provides
    @UsersScope
    fun provideUsersRepository(retrofit: Retrofit): UsersRepository {
        return UsersRepositoryImpl(retrofit)
    }


    @Provides
    @UsersScope
    fun provideUsersFactory(
        settingManager: SettingManager,
        usersLocalCache: UsersLocalCache,
        usersRepository: UsersRepository
    ): UsersViewModelImpl.Factory {
        return UsersViewModelImpl.Factory(
            settingManager = settingManager,
            usersLocalCache = usersLocalCache,
            usersRepository = usersRepository
        )
    }

    @Provides
    @UsersScope
    fun provideUserInfoFactory(
        settingManager: SettingManager,
        usersLocalCache: UsersLocalCache,
        usersRepository: UsersRepository
    ): UserInfoViewModelImpl.Factory {
        return UserInfoViewModelImpl.Factory(
            settingManager = settingManager,
            usersLocalCache = usersLocalCache,
            usersRepository = usersRepository
        )
    }

    @Provides
    @UsersScope
    fun provideUsersBookmarkFactory(
        settingManager: SettingManager,
        usersLocalCache: UsersLocalCache
    ): UsersBookmarkViewModelImpl.Factory {
        return UsersBookmarkViewModelImpl.Factory(
            settingManager = settingManager,
            usersLocalCache = usersLocalCache
        )
    }
}