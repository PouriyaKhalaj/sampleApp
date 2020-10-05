package ir.co.avatar.ui

import android.content.Intent
import ir.co.avatar.R
import ir.co.common.base.ActivityBase

class MainActivity : ActivityBase() {
    override fun getResourceLayoutId(): Int = R.layout.activity_main

    override fun onStartActivity(intent: Intent, finished: Boolean) {
    }
}