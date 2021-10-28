package com.konopelko.explorex.di.component

import com.konopelko.explorex.di.module.network.NetworkModule
import com.konopelko.explorex.di.module.repository.RepositoryModule
import com.konopelko.explorex.di.module.usecase.UseCaseModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, UseCaseModule::class, RepositoryModule::class])
interface ApplicationComponent {
}