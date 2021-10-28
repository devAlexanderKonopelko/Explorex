package com.konopelko.explorex.presentation.view.home

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.konopelko.explorex.ExplorexApplication
import com.konopelko.explorex.R

class HomeActivity: FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as ExplorexApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }
}