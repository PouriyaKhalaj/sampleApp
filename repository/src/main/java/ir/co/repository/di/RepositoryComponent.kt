package ir.co.repository.di

import com.google.gson.Gson
import dagger.Component
import ir.co.common.di.CommonModule
import ir.co.common.helper.ActivityLifeCycle
import ir.co.common.helper.SecurityHelper
import ir.co.common.helper.SerializationHelper
import ir.co.common.helper.SettingManager
import retrofit2.Retrofit
import javax.inject.Singleton


@Component(
    modules = [NetworkModule::class, CommonModule::class]
)
@Singleton
interface RepositoryComponent {
    fun provideGson(): Gson
    fun getRetrofit(): Retrofit
    fun provideSecurityHelper(): SecurityHelper
    fun provideSettingManager(): SettingManager
    fun provideActivityLifeCycle(): ActivityLifeCycle
    fun provideSerializationHelper(): SerializationHelper
}
