package ir.co.sample


import android.annotation.SuppressLint
import ir.co.common.base.BaseApp
import ir.co.common.helper.SettingManager
import ir.co.repository.di.DataBaseModule
import ir.co.repository.di.getRepositoryComponent
import ir.co.sample.di.DaggerAppComponent
import javax.inject.Inject


class RootApp : BaseApp() {

    @Inject
    lateinit var settingManager: SettingManager

    @SuppressLint("HardwareIds")
    override fun inject() {
        DaggerAppComponent.builder()
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