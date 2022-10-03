package com.hmmelton.firebasedemo.binding

import com.hmmelton.firebasedemo.MainActivity
import dagger.Component

@Component(modules = [AuthenticationModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}