package ir.co.repository.repositories

import ir.co.common.dto.UsersResponse
import ir.co.repository.remote_services.RemoteUsersService
import retrofit2.Retrofit

abstract class UsersRepository {
    abstract suspend fun users(page: Int): UsersResponse
}


class UsersRepositoryImpl constructor(private val retrofit: Retrofit) : UsersRepository() {

    override suspend fun users(page: Int): UsersResponse =
        retrofit.create(RemoteUsersService::class.java)
            .usersAsync(page = page)
            .await()
}