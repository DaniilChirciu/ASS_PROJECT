package ru.app.pawbuddy.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.app.pawbuddy.domain.repository.PetRepository
import ru.app.pawbuddy.domain.usecase.AddPetUseCase
import ru.app.pawbuddy.domain.usecase.GetPetsUseCase

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides fun provideAddPetUseCase(repo: PetRepository) = AddPetUseCase(repo)
    @Provides fun provideGetPetsUseCase(repo: PetRepository) = GetPetsUseCase(repo)
}
