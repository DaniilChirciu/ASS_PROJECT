package ru.app.pawbuddy.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.app.pawbuddy.domain.model.PetData
import ru.app.pawbuddy.domain.repository.PetRepository

class GetPetsUseCase(private val repo: PetRepository) {
    operator fun invoke(): Flow<List<PetData>> = repo.getPetsStream()
}
