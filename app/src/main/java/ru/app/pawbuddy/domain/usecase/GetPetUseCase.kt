package ru.app.pawbuddy.domain.usecase

import ru.app.pawbuddy.domain.model.PetData
import ru.app.pawbuddy.domain.repository.PetRepository

class GetPetUseCase(private val repo: PetRepository) {
    suspend operator fun invoke(id: String) = repo.getPetById(id)
}
