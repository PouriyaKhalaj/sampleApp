package ir.co.repository.remote_services

import ir.co.common.dto.UsersResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteUsersService {

    @GET("users")
    fun usersAsync(
        @Query("page") page: Int
    ): Deferred<UsersResponse>
}