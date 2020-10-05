package ir.co.repository.network.interceptor

import ir.co.common.helper.SettingManager
import okhttp3.Interceptor
import okhttp3.Response

class RequestHeaderInterceptor(
    private val settingManager: SettingManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }
}

