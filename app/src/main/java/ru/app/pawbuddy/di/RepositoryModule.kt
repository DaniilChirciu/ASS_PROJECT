package ru.app.pawbuddy.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.app.pawbuddy.data.repository.PetRepositoryImpl
import ru.app.pawbuddy.domain.repository.PetRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPetRepository(impl: PetRepositoryImpl): PetRepository
}
