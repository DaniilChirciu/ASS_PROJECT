package ru.app.pawbuddy.domain.usecase

import ru.app.pawbuddy.domain.model.PetData
import ru.app.pawbuddy.domain.repository.PetRepository

class UpdatePetUseCase(private val repo: PetRepository) {
    suspend operator fun invoke(pet: PetData) = repo.updatePet(pet)
}
