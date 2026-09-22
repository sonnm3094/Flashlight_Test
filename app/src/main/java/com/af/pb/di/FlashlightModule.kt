package com.af.pb.di

import com.af.pb.data.repositories.FlashlightRepositoryImpl
import com.af.pb.data.repositories.LedRepositoryImpl
import com.af.pb.data.repositories.ScreenLightRepositoryImpl
import com.af.pb.domain.repository.FlashlightRepository
import com.af.pb.domain.repository.LedRepository
import com.af.pb.domain.repository.ScreenLightRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FlashlightModule {

    @Binds
    @Singleton
    abstract fun bindFlashlightRepository(
        impl: FlashlightRepositoryImpl
    ): FlashlightRepository

    @Binds
    @Singleton
    abstract fun bindScreenLightRepository(
        impl: ScreenLightRepositoryImpl
    ): ScreenLightRepository

    @Binds
    @Singleton
    abstract fun bindLedRepository(
        impl: LedRepositoryImpl
    ): LedRepository
}
