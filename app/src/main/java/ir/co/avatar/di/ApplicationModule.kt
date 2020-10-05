package ir.co.avatar.di


import dagger.Module
import dagger.Provides
import ir.co.common.helperImpl.ActivityLifeCycleImpl


@Module
class ApplicationModule() {

    @AppScope
    @Provides
    fun provideLifeCycleActivity(): ActivityLifeCycleImpl {
        return ActivityLifeCycleImpl()
    }
}