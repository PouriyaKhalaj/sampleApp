package ir.co.sample.ui

import android.content.Intent
import ir.co.sample.R
import ir.co.common.base.ActivityBase

class MainActivity : ActivityBase() {
    override fun getResourceLayoutId(): Int = R.layout.activity_main

    override fun onStartActivity(intent: Intent, finished: Boolean) {
    }
}