package ir.co.common.helperImpl

import android.app.Activity
import android.os.Bundle
import ir.co.common.helper.ActivityLifeCycle

interface LifecycleHandler {
    fun onActivityDestroying(act: Activity)
}

class ActivityLifeCycleImpl : ActivityLifeCycle {

    private var activity: Activity? = null

    var handler: LifecycleHandler? = null

    override fun getTopActivity(): Activity? {
        return activity
    }

    override fun onActivityPaused(act: Activity) {}

    override fun onActivityResumed(act: Activity) {
        activity = act
    }

    override fun onActivityStarted(act: Activity) {
        activity = act
    }

    override fun onActivityDestroyed(act: Activity) {
        act.let {
            if (handler != null) {
                handler?.onActivityDestroying(it)
            }
        }
    }

    override fun onActivitySaveInstanceState(act: Activity, p1: Bundle) {}

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {}

    fun setHandlerLifecycle(handler: LifecycleHandler) {
        this.handler = handler
    }

}