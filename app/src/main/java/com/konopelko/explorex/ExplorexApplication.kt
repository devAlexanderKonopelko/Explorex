package com.konopelko.explorex

import android.app.Application
import com.konopelko.explorex.di.component.ApplicationComponent
import com.konopelko.explorex.di.component.DaggerApplicationComponent

class ExplorexApplication: Application() {

    val appComponent: ApplicationComponent = DaggerApplicationComponent.create()
}