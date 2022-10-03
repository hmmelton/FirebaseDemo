package com.hmmelton.firebasedemo

import android.app.Application
import com.hmmelton.firebasedemo.binding.AppComponent
import com.hmmelton.firebasedemo.binding.DaggerAppComponent

open class FirebaseDemoApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.create()
    }
}