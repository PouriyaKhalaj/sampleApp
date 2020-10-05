package ir.co.repository.di

import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import ir.co.common.BuildConfig
import ir.co.common.di.CommonModule
import ir.co.common.helper.SettingManager
import ir.co.repository.network.interceptor.RequestHeaderInterceptor
import ir.co.repository.network.interceptor.ResponseHeaderInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


@Module
class NetworkModule() {

    private fun provideOkHttp(
        settingManager: SettingManager,
    ): OkHttpClient {
        val client = OkHttpClient.Builder().addInterceptor(
            RequestHeaderInterceptor(
                settingManager = settingManager
            )
        )
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
        client.addInterceptor(HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }).addInterceptor(ResponseHeaderInterceptor(settingManager = settingManager))
        return client.build()
    }

    @Provides
    fun provideRetrofit(settingManager: SettingManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.baseUrl)
            .client(provideOkHttp(settingManager))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}

fun getRepositoryComponent(
    preferences: SharedPreferences
): RepositoryComponent {
    return DaggerRepositoryComponent.builder()
        .networkModule(NetworkModule())
        .commonModule(CommonModule(preferences))
        .build()
}
