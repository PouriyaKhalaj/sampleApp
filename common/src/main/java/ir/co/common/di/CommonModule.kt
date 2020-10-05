package ir.co.common.di

import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ir.co.common.helper.ActivityLifeCycle
import ir.co.common.helper.SecurityHelper
import ir.co.common.helper.SerializationHelper
import ir.co.common.helper.SettingManager
import ir.co.common.helperImpl.ActivityLifeCycleImpl
import ir.co.common.helperImpl.SecurityHelperImpl
import ir.co.common.helperImpl.SerializationHelperImpl
import ir.co.common.helperImpl.SettingManagerImpl

@Module
class CommonModule(private val preferences: SharedPreferences) {

    @Provides
    fun provideGson(): Gson = Gson()

    @Provides
    fun provideSecurityHelper(): SecurityHelper = SecurityHelperImpl()

    @Provides
    fun provideActivityLifeCycle(): ActivityLifeCycle = ActivityLifeCycleImpl()

    @Provides
    fun provideSerializationHelper(gson: Gson): SerializationHelper = SerializationHelperImpl(gson)

    @Provides
    fun provideSettingManager(securityHelper: SecurityHelper): SettingManager =
        SettingManagerImpl(preferences, securityHelper)


}