package ir.co.repository.network.interceptor

import ir.co.common.helper.SettingManager
import okhttp3.Interceptor
import okhttp3.Response

class ResponseHeaderInterceptor(
    private val settingManager: SettingManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(request)
    }
}