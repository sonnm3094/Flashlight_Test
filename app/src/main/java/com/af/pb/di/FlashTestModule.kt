package com.af.pb.di

import com.af.pb.data.repositories.FlashTestRepositoryImpl
import com.af.pb.domain.repository.FlashTestRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FlashTestModule {

    @Binds
    @Singleton
    abstract fun bindFlashTestRepository(
        impl: FlashTestRepositoryImpl
    ): FlashTestRepository
}
