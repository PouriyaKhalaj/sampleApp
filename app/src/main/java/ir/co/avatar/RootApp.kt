package ir.co.avatar


import android.annotation.SuppressLint
import ir.co.avatar.di.ApplicationModule
import ir.co.avatar.di.DaggerAppComponent
import ir.co.common.base.BaseApp
import ir.co.common.helper.SettingManager
import ir.co.repository.di.getRepositoryComponent
import javax.inject.Inject


class RootApp : BaseApp() {

    @Inject
    lateinit var settingManager: SettingManager

    @SuppressLint("HardwareIds")
    override fun inject() {
        DaggerAppComponent.builder()
            .applicationModule(ApplicationModule())
            .repositoryComponent(
                getRepositoryComponent(preferences = getPreferenceManager())
            )
            .build()
            .inject(this)
    }

    override fun onCreate() {
        super.onCreate()
        changeLanguage(this, "fa")
    }
}