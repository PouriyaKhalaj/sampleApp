package ir.co.common.helper

import android.app.Activity
import android.app.Application

interface ActivityLifeCycle : Application.ActivityLifecycleCallbacks {

    fun getTopActivity(): Activity?

}